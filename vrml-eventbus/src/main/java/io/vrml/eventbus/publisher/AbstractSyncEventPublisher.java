package io.vrml.eventbus.publisher;

import io.vrml.core.serialization.Serialization;
import io.vrml.eventbus.event.EventBusEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * To publish the eventbus event sync.
 *
 * @param <T> the event type
 */
@Slf4j
public abstract class AbstractSyncEventPublisher<T extends EventBusEvent> extends AbstractEventPublisher {

    /**
     * Publish the event.
     *
     * @param event the event
     */
    public void publishEvent(T event) {
        try {
            this.applicationContext.publishEvent(event);
        } catch (Exception e) {
            log.error("[SyncEventPublisher] publish event[{}] error.", Serialization.GSON.toJson(event), e);
        }
    }
}