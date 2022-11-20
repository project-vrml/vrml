package group.rxcloud.vrml.shutdown;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.MoreExecutors;
import io.vavr.CheckedRunnable;
import io.vavr.Lazy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple and easy-to-use shutdownhook mechanism.
 */
public final class ShutdownHooks {

    /**
     * Tuned experience values for default out-of-the-box use.
     */
    private static int TUNED_TERMINATION_TIMEOUT_MS = 3000;
    private static final Duration TUNED_TERMINATION_TIMEOUT = Duration.ofMillis(TUNED_TERMINATION_TIMEOUT_MS);

    /**
     * Allow users to set the default interrupt time by themselves.
     */
    public static void setTunedTerminationTimeoutMs(int tunedTerminationTimeoutMs) {
        TUNED_TERMINATION_TIMEOUT_MS = tunedTerminationTimeoutMs;
    }

    /**
     * Shutdown thread properties.
     */
    private static final String TUNED_SHUTDOWN_THREAD_NAME = "vrml-shutdown-thread-";
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);

    /**
     * Daemon task properties.
     */
    private static final Lazy<List<Runnable>> DAEMON_TASKS = Lazy.of(ArrayList::new);
    /**
     * Use Lazy to ensure it is only registered once and fetch all pending tasks when it actually runs.
     */
    private static final Lazy<Void> DAEMON_HOOK = Lazy.of(() -> {
        CheckedRunnable hookFunc = () -> {
            // Use threads to process daemon tasks as much as possible, so you can't set up a queue to avoid a backlog.
            ExecutorService executorService = Executors.newCachedThreadPool();

            List<Runnable> runnables = DAEMON_TASKS.get();
            for (Runnable runnable : runnables) {
                // Use a new thread to process pending task
                executorService.submit(runnable);
            }

            // Let the tasks get execution time
            Thread.sleep(1);

            // Close the thread pool and set the end time
            executorService.shutdown();
            executorService.awaitTermination(TUNED_TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        };

        // first time registration only
        addShutdownHook(hookFunc);
        return null;
    });

    /**
     * Add a shutdownhook, it will run until it finishes, so don't add blocking
     * operations.
     * <p>
     * To use shutdownhook correctly, make sure you understand the mechanics of
     * the following method:
     * {@link ExecutorService#shutdown()}
     * {@link Thread#interrupt()}
     * {@code ApplicationShutdownHooks#runHooks()}
     */
    public static void addShutdownHook(CheckedRunnable runnable) {
        Runnable hookFunc = () -> {
            try {
                // We'd like to log progress and failures that may arise in the
                // following code, but unfortunately the behavior of logging
                // is undefined in shutdown hooks.
                // This is because the logging code installs a shutdown hook of its
                // own. See Cleaner class inside {@link LogManager}.
                runnable.run();
            } catch (Throwable ignore) {
                // We're shutting down anyway, so just ignore.
            }
        };

        // t.setDaemon(false);
        // see {@link DefaultThreadFactory}
        Thread thread = MoreExecutors.platformThreadFactory()
                .newThread(hookFunc);
        thread.setName(TUNED_SHUTDOWN_THREAD_NAME + THREAD_NUMBER.getAndIncrement());

        Runtime.getRuntime().addShutdownHook(thread);
    }

    /**
     * Add a shutdownhook with daemon threadpool, It will run until the interrupt
     * time is over, so the task may not be completed.
     * <p>
     * To use shutdownhook correctly, make sure you understand the mechanics of
     * the following method:
     * {@link ExecutorService#shutdown()}
     * {@link Thread#interrupt()}
     * {@code ApplicationShutdownHooks#runHooks()}
     */
    @Beta
    public static void addShutdownHookDaemon(CheckedRunnable runnable) {
        Runnable hookFunc = () -> {
            try {
                runnable.run();
            } catch (Throwable ignore) {
                // We're shutting down anyway, so just ignore.
            }
        };

        DAEMON_TASKS.get().add(hookFunc);
        // first time registration only
        DAEMON_HOOK.get();
    }

    /**
     * Add a shutdownhook for ExecutorService with default timeout, it will run
     * until it finishes, so don't add blocking operations.
     * <p>
     * To use shutdownhook correctly, make sure you understand the mechanics of
     * the following method:
     * {@link ExecutorService#shutdown()}
     * {@link Thread#interrupt()}
     * {@code ApplicationShutdownHooks#runHooks()}
     */
    public static void addShutdownHook(ExecutorService service) {
        MoreExecutors.addDelayedShutdownHook(service, TUNED_TERMINATION_TIMEOUT);
    }

    /**
     * Add a shutdownhook for ExecutorService with custom timeout, it will run
     * until it finishes, so don't add blocking operations.
     * <p>
     * To use shutdownhook correctly, make sure you understand the mechanics of
     * the following method:
     * {@link ExecutorService#shutdown()}
     * {@link Thread#interrupt()}
     * {@code ApplicationShutdownHooks#runHooks()}
     */
    public static void addShutdownHook(ExecutorService service, Duration terminationTimeout) {
        MoreExecutors.addDelayedShutdownHook(service, terminationTimeout);
    }
}
