package com.vavr.func.work.eventbus.event;

import io.vavr.Tuple;

import java.lang.reflect.InvocationTargetException;

/**
 * The type Abstract async tuple event.
 *
 * @param <Values> the type of tuple
 */
public abstract class AbstractAsyncTupleEvent<Values extends Tuple, Type extends AbstractAsyncTupleEvent> extends AbstractTupleEvent<Values> implements AsyncEventCloneable<Type> {

    /**
     * Create a new Abstract Tuple Event.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param tuples the tuples
     */
    public AbstractAsyncTupleEvent(Object source, Values tuples) {
        super(source, tuples);
    }

    /**
     * Clone tuple.
     *
     * @return the tuple
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     */
    public abstract Values cloneTuple() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
