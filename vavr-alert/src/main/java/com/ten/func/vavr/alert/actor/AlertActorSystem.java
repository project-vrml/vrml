package com.ten.func.vavr.alert.actor;

import com.google.common.util.concurrent.MoreExecutors;
import com.ten.func.vavr.alert.config.AlertConfiguration;
import com.ten.func.vavr.core.beans.SpringContextConfigurator;
import com.ten.func.vavr.core.tags.Important;
import com.ten.func.vavr.core.tags.TodoUp;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The alert actor system.
 */
@Slf4j
public abstract class AlertActorSystem {

    /**
     * Async alerts switch
     */
    private static boolean asyncAlerts = false;

    /**
     * The serial number of thread in thread pool.
     */
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

    /**
     * The max number of task in thread pool.
     */
    private static final Integer MAX_EVENT_SIZE = 1024;

    /**
     * The max seconds of keep alive time
     */
    private static final long MAX_KEEP_ALIVE_TIME_SECONDS = 10;

    /**
     * The thread pool size.
     */
    private static final int DEFAULT_THREAD_POOL_SIZE = 1;
    private static final int MAX_THREAD_POOL_SIZE = 2;

    private static final int DEFAULT_SHUTDOWN_WAIT_SECONDS = 5;

    /**
     * Async alerts executor
     */
    private static final ExecutorService executorService;

    static {
        executorService = new ThreadPoolExecutor(
                DEFAULT_THREAD_POOL_SIZE,
                MAX_THREAD_POOL_SIZE,
                MAX_KEEP_ALIVE_TIME_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(MAX_EVENT_SIZE),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("common-alert-thread-" + THREAD_COUNTER.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());

        MoreExecutors.addDelayedShutdownHook(executorService, DEFAULT_SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Alerts configurator
     */
    private static AlertConfiguration configuration;

    private static final Object INIT_LOCK = new Object();

    /**
     * Alerts container
     */
    private static Map<Class<? extends AlertMessage>, AlertActor<? extends AlertMessage>> ACTOR_CONTAINER;

    @SuppressWarnings(value = "unchecked")
    private static void initSpringContextConfig() {
        if (ACTOR_CONTAINER == null || configuration == null) {
            synchronized (INIT_LOCK) {
                if (ACTOR_CONTAINER == null || configuration == null) {
                    try {
                        configuration = SpringContextConfigurator.getBean(AlertConfiguration.class);

                        // load alert actors from spring context
                        Map<String, AlertActor> beans = SpringContextConfigurator.getBeans(AlertActor.class);
                        ACTOR_CONTAINER = beans.values().stream()
                                .collect(Collectors.toMap(
                                        alertActor -> (Class<? extends AlertMessage>) ((ParameterizedType) alertActor.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                                        alertActor -> (AlertActor<? extends AlertMessage>) alertActor));
                    } catch (Throwable throwable) {
                        log.error("Alert actor system init spring context configuration failure.", throwable);
                    }
                }
            }
        }
    }

    @Important(description = "The only way to get container")
    private static Map<Class<? extends AlertMessage>, AlertActor<? extends AlertMessage>> getActorContainer() {
        AlertActorSystem.initSpringContextConfig();
        return ACTOR_CONTAINER;
    }

    @Important(description = "The only way to get configuration")
    private static AlertConfiguration getConfiguration() {
        AlertActorSystem.initSpringContextConfig();
        return configuration;
    }

    /**
     * Send message to actor.
     *
     * @param <T>     the message type
     * @param message the message
     */
    @SuppressWarnings(value = "unchecked")
    private static <T extends AlertMessage> void send(T message) {
        AlertActor<T> alertActor = (AlertActor<T>) getActorContainer().get(message.getClass());
        alertActor.receive(message);
    }

    /**
     * Tell async message.
     *
     * @param message the immutable message
     */
    @TodoUp(optimizeDesc = "make message immutable")
    public static void tell(AlertMessage message) {
        if (message != null) {
            if (getConfiguration().isAlertEnable(message)) {
                if (asyncAlerts) {
                    executorService.submit(() -> AlertActorSystem.send(message));
                } else {
                    AlertActorSystem.send(message);
                }
            }
        }
    }
}
