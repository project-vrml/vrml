package io.vrml.eventbus.event.process;

import io.vrml.eventbus.event.AsyncEventBusEvent;

/**
 * The abstract async process event.
 *
 * @param <Request>  the process request type
 * @param <Response> the process response type
 * @param <Type>     the event type
 */
public abstract class AbstractAsyncProcessEvent<Request, Response, Type extends AbstractAsyncProcessEvent<Request, Response, Type>>
        extends AbstractProcessEvent<Request, Response> implements AsyncEventBusEvent<Type> {

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
     */
    public abstract Request cloneRequest();

    /**
     * Clone response.
     *
     * @return the response
     */
    public abstract Response cloneResponse();
}
