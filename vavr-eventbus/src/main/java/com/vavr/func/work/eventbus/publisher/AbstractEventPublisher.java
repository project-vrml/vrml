package com.vavr.func.work.eventbus.publisher;


import com.google.gson.Gson;
import com.vavr.func.work.eventbus.event.AsyncEventCloneable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * To publish the event.
 *
 * @param <T> the event type
 */
@Slf4j
public abstract class AbstractEventPublisher<T extends AsyncEventCloneable> implements ApplicationContextAware {

    /**
     * The constant GSON.
     */
    protected static final Gson GSON = new Gson();

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
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;

    /**
     * The thread pool for dispatch events.
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(
            DEFAULT_THREAD_POOL_SIZE,
            DEFAULT_THREAD_POOL_SIZE,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(MAX_EVENT_SIZE),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("event-publisher-" + THREAD_COUNTER.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Publish event.
     *
     * @param event the event
     */
    public void publishEvent(T event) {
        try {
            this.applicationContext.publishEvent(event);
        } catch (Exception e) {
            log.warn("[SyncEventPublisher] event[{}] publish error.", GSON.toJson(event), e);
        }
    }

    /**
     * Publish event async.
     *
     * @param event the event
     */
    public void publishEventAsync(T event) {
        Optional<T> cloneEventOp = this.tryCloneEvent(event);
        cloneEventOp.ifPresent(cloneEvent -> {
            executorService.submit(() -> {
                try {
                    this.applicationContext.publishEvent(cloneEvent);
                } catch (Exception e) {
                    log.warn("[AsyncEventPublisher] event[{}] publish error.", GSON.toJson(event), e);
                }
            });
        });
    }

    private Optional<T> tryCloneEvent(T event) {
        try {
            return Optional.ofNullable(this.cloneEvent(event));
        } catch (Exception e) {
            log.warn("[AsyncEventPublisher] [CloneAsyncEvent] event[{}] clone error.", GSON.toJson(event), e);
            return Optional.empty();
        }
    }

    /**
     * Clone event.
     *
     * @param event the event
     * @return the new event
     */
    protected abstract T cloneEvent(T event);
}