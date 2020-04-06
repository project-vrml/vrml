package com.tem.func.vavr.eventbus.event;

import io.vavr.Tuple;
import org.springframework.context.ApplicationEvent;

/**
 * The abstract tuple event.
 *
 * @param <Values> the type of tuple
 */
public abstract class AbstractTupleEvent<Values extends Tuple> extends ApplicationEvent {

    private Values tuples;

    /**
     * Create a new Abstract Tuple Event.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param tuples the tuples
     */
    public AbstractTupleEvent(Object source, Values tuples) {
        super(source);
        this.tuples = tuples;
    }

    /**
     * Gets tuples.
     *
     * @return the tuples
     */
    public Values getTuples() {
        return tuples;
    }

    /**
     * Sets tuples.
     *
     * @param tuples the tuples
     */
    public void setTuples(Values tuples) {
        this.tuples = tuples;
    }
}
