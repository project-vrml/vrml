package group.rxcloud.vrml.eventbus.publisher;

import group.rxcloud.vrml.core.serialization.Serialization;
import group.rxcloud.vrml.eventbus.event.AsyncEventBusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * To publish the eventbus event async.
 *
 * @param <Type> the async event type
 */
public abstract class AbstractAsyncEventPublisher<Type extends AsyncEventBusEvent<Type>> extends AbstractEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AbstractAsyncEventPublisher.class);

    /**
     * The serial number of thread in thread pool.
     */
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

    /**
     * The max number of task in thread pool.
     */
    private static final Integer MAX_EVENT_SIZE = 1024;

    /**
     * The default thread pool size.
     */
    private static final int DEFAULT_THREAD_POOL_CORE_SIZE = 4;
    private static final int DEFAULT_THREAD_POOL_MAX_SIZE = 16;

    /**
     * The thread pool for dispatch events.
     */
    private volatile ExecutorService executorService;

    protected ExecutorService getExecutorService() {
        if (executorService == null) {
            synchronized (this) {
                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(
                            DEFAULT_THREAD_POOL_CORE_SIZE,
                            DEFAULT_THREAD_POOL_MAX_SIZE,
                            0L,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingDeque<>(MAX_EVENT_SIZE),
                            r -> {
                                Thread thread = new Thread(r);
                                thread.setName("eventbus-publisher-" + THREAD_COUNTER.incrementAndGet());
                                thread.setDaemon(true);
                                return thread;
                            },
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }
        return executorService;
    }

    /**
     * Publish the event async.
     *
     * @param event the event
     */
    public void publishEventAsync(Type event) {
        Optional<Type> cloneEventOp = this.tryCloneEvent(event);
        cloneEventOp.ifPresent(cloneEvent ->
                getExecutorService().submit(wrapRunnable(cloneEvent)));
    }

    protected Runnable wrapRunnable(Type cloneEvent) {
        return () -> {
            try {
                this.applicationContext.publishEvent(cloneEvent);
            } catch (Exception e) {
                log.error("[Vrml][AsyncEventPublisher] publish event[{}] error.", Serialization.GSON.toJson(cloneEvent), e);
            }
        };
    }

    private Optional<Type> tryCloneEvent(Type event) {
        try {
            return Optional.ofNullable(event.cloneEvent());
        } catch (Exception e) {
            log.error("[Vrml][AsyncEventPublisher] clone event[{}] error.", Serialization.GSON.toJson(event), e);
            return Optional.empty();
        }
    }
}