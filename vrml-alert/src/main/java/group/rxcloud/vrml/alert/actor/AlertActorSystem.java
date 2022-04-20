package group.rxcloud.vrml.alert.actor;

import group.rxcloud.vrml.alert.config.AlertConfiguration;
import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import group.rxcloud.vrml.core.tags.Important;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The alert actor system.
 */
@Slf4j
public abstract class AlertActorSystem {

    // -- Alert async

    /**
     * Async alerts executor.
     */
    private static volatile ExecutorService executorService;

    /**
     * Sets executor service.
     *
     * @param executorService the executor service
     */
    public static void setExecutorService(ExecutorService executorService) {
        AlertActorSystem.executorService = executorService;
    }

    // -- Alert configuration

    /**
     * Alerts configuration.
     */
    private static volatile AlertConfiguration configuration;

    @Important(important = "The only way to get configuration")
    private static AlertConfiguration getConfiguration() {
        AlertActorSystemInitial.initSpringContextConfig();
        return configuration;
    }

    // -- Alert actor container

    /**
     * Alerts container.
     */
    private static volatile Map<Class<? extends AlertMessage>, AlertActor<? extends AlertMessage>> ACTOR_CONTAINER;

    @Important(important = "The only way to get container")
    private static Map<Class<? extends AlertMessage>, AlertActor<? extends AlertMessage>> getActorContainer() {
        AlertActorSystemInitial.initSpringContextConfig();
        return ACTOR_CONTAINER;
    }

    // -- Alert API

    /**
     * Send the message to specific actor.
     *
     * @param <T>     the alert message type
     * @param message the alert message
     */
    @SuppressWarnings(value = "unchecked")
    private static <T extends AlertMessage> void send(final T message) {
        AlertActor<T> alertActor = (AlertActor<T>) getActorContainer().get(message.getClass());
        if (alertActor != null) {
            alertActor.receive(message);
        } else {
            // illegal situation
            log.warn("[Vrml][Alerts.AlertActorSystem.send] alert actor not found by message type[{}]", message.getClass().getSimpleName());
        }
    }

    /**
     * Tell alert message to specific actor.
     *
     * @param message the immutable message
     */
    public static void tell(final AlertMessage message) {
        if (message != null) {
            AlertConfiguration configuration = getConfiguration();
            if (configuration != null) {
                if (configuration.isAlertEnable(message)) {
                    if (configuration.isAlertAsync()) {
                        AlertActorSystemInitial.initAsyncAlertPool();
                        executorService.submit(() -> {
                            AlertActorSystem.send(message);
                        });
                    } else {
                        AlertActorSystem.send(message);
                    }
                }
            } else {
                // illegal situation
                log.warn("[Vrml][Alerts.AlertActorSystem.tell] alert configuration not found");
            }
        }
    }

    // -- Alert actor system initial

    /**
     * AlertActorSystem configuration initialization processor
     */
    private static final class AlertActorSystemInitial {

        /**
         * The serial number of thread in thread pool.
         */
        private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);

        /**
         * The max number of task in thread pool.
         */
        private static final Integer MAX_QUEUE_SIZE = 1024;

        /**
         * The max seconds of keep alive time
         */
        private static final long MAX_KEEP_ALIVE_TIME_SECONDS = 10;

        /**
         * The thread pool size.
         */
        private static final int DEFAULT_THREAD_POOL_SIZE = 1;
        private static final int MAX_THREAD_POOL_SIZE = 2;

        /**
         * The thread pool shutdown params.
         */
        private static final int DEFAULT_SHUTDOWN_WAIT_SECONDS = 3;

        /**
         * Init alert async thread pool when {@link AlertConfiguration#isAlertAsync()} is true.
         */
        private static void initAsyncAlertPool() {
            if (getConfiguration().isAlertAsync()) {
                if (executorService == null) {
                    synchronized (AlertActorSystemInitial.class) {
                        if (executorService == null) {
                            setExecutorService(new ThreadPoolExecutor(
                                    DEFAULT_THREAD_POOL_SIZE,
                                    MAX_THREAD_POOL_SIZE,
                                    MAX_KEEP_ALIVE_TIME_SECONDS,
                                    TimeUnit.SECONDS,
                                    new LinkedBlockingDeque<>(MAX_QUEUE_SIZE),
                                    r -> {
                                        Thread thread = new Thread(r);
                                        thread.setName("common-alert-thread-" + THREAD_COUNTER.incrementAndGet());
                                        thread.setDaemon(true);
                                        return thread;
                                    },
                                    new ThreadPoolExecutor.CallerRunsPolicy()));

                            // shutdown hook
                            MoreExecutors.addDelayedShutdownHook(executorService, DEFAULT_SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        }

        /**
         * Init alert configuration and alert container.
         */
        @SuppressWarnings(value = "unchecked")
        private static void initSpringContextConfig() {
            if (ACTOR_CONTAINER == null || configuration == null) {
                synchronized (AlertActorSystemInitial.class) {
                    if (ACTOR_CONTAINER == null || configuration == null) {
                        try {
                            // load alert configuration from spring context
                            configuration = SpringContextConfigurator.getBean(AlertConfiguration.class);

                            // load alert actor beans from spring context
                            Map<String, AlertActor> beans = SpringContextConfigurator.getBeans(AlertActor.class);

                            // map to generic type <? extends AlertMessage>
                            Stream<AlertActor<? extends AlertMessage>> alertActorStream = beans.values().stream()
                                    .map(alertActor -> (AlertActor<? extends AlertMessage>) alertActor);
                            ACTOR_CONTAINER = mapToAlertContainer(alertActorStream);
                        } catch (Exception exception) {
                            log.error("[Vrml][Alerts.AlertActorSystem.initSpringContextConfig] alert actor system init spring context configuration failure.", exception);
                        }
                    }
                }
            }
        }

        /**
         * Map {@code Collection<AlertActor>} to {@code Map<Type(AlertMessage), AlertActor>}.
         *
         * @param actorContainer stream of collection
         */
        @SuppressWarnings(value = "unchecked")
        private static Map<Class<? extends AlertMessage>, AlertActor<? extends AlertMessage>> mapToAlertContainer(Stream<AlertActor<? extends AlertMessage>> actorContainer) {
            return actorContainer
                    .collect(Collectors.toMap(
                            alertActor -> (Class<? extends AlertMessage>) ((ParameterizedType) alertActor.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                            alertActor -> (AlertActor<? extends AlertMessage>) alertActor));
        }
    }
}
