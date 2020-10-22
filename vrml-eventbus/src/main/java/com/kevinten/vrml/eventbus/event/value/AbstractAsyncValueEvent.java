package com.kevinten.vrml.eventbus.event.value;

import com.kevinten.vrml.eventbus.event.AsyncEventBusEvent;

/**
 * The abstract async value event.
 *
 * @param <Value> the value type
 * @param <Type>  the event type
 */
public abstract class AbstractAsyncValueEvent<Value, Type extends AbstractAsyncValueEvent<Value, Type>>
        extends AbstractValueEvent<Value> implements AsyncEventBusEvent<Type> {

    /**
     * Instantiates a new Abstract async value event.
     *
     * @param source the source
     * @param value  the value
     */
    public AbstractAsyncValueEvent(Object source, Value value) {
        super(source, value);
    }

    /**
     * Clone value.
     *
     * @return the value
     */
    public abstract Value cloneValue();
}
