package group.rxcloud.vrml.eventbus.event.value;

import group.rxcloud.vrml.eventbus.event.EventBusEvent;
import org.springframework.context.ApplicationEvent;

/**
 * The abstract value event.
 *
 * @param <Value> the value type
 */
public abstract class AbstractValueEvent<Value> extends ApplicationEvent implements EventBusEvent {

    private final Value value;

    /**
     * Instantiates a new Abstract value event.
     *
     * @param source the source
     * @param value  the value
     */
    public AbstractValueEvent(Object source, Value value) {
        super(source);
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Value getValue() {
        return value;
    }
}
