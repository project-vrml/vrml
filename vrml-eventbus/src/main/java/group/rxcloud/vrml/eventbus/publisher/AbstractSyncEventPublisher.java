package group.rxcloud.vrml.eventbus.publisher;

import group.rxcloud.vrml.core.serialization.Serialization;
import group.rxcloud.vrml.eventbus.event.EventBusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To publish the eventbus event sync.
 *
 * @param <T> the event type
 */
public abstract class AbstractSyncEventPublisher<T extends EventBusEvent> extends AbstractEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AbstractSyncEventPublisher.class);

    /**
     * Publish the event.
     *
     * @param event the event
     */
    public void publishEvent(T event) {
        try {
            this.applicationContext.publishEvent(event);
        } catch (Exception e) {
            log.error("[Vrml][SyncEventPublisher] publish event[{}] error.", Serialization.GSON.toJson(event), e);
        }
    }
}