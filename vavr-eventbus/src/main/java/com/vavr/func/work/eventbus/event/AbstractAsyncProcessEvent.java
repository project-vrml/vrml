package com.vavr.func.work.eventbus.event;

import java.lang.reflect.InvocationTargetException;

/**
 * The type Abstract async process event.
 *
 * @param <Request>  the type parameter
 * @param <Response> the type parameter
 * @param <Type>     the type parameter
 */
public abstract class AbstractAsyncProcessEvent<Request, Response, Type extends AbstractAsyncProcessEvent> extends AbstractProcessEvent<Request, Response> implements AsyncEventCloneable<Type> {

    /**
     * Create a new Abstract Process Event.
     *
     * @param source   the object on which the event initially occurred (never {@code null})
     * @param request  the request
     * @param response the response
     */
    public AbstractAsyncProcessEvent(Object source, Request request, Response response) {
        super(source, request, response);
    }

    /**
     * Clone request.
     *
     * @return the request
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     */
    public abstract Request cloneRequest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    /**
     * Clone response.
     *
     * @return the response
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     */
    public abstract Response cloneResponse() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
