package com.tem.func.vavr.eventbus.event;

import org.springframework.context.ApplicationEvent;

/**
 * The abstract process event.
 *
 * @param <Request>  the type process request
 * @param <Response> the type process response
 */
public abstract class AbstractProcessEvent<Request, Response> extends ApplicationEvent {

    private Request request;

    private Response response;

    /**
     * Create a new Abstract Process Event.
     *
     * @param source   the object on which the event initially occurred (never {@code null})
     * @param request  the request
     * @param response the response
     */
    public AbstractProcessEvent(Object source, Request request, Response response) {
        super(source);
        this.request = request;
        this.response = response;
    }

    /**
     * Gets request.
     *
     * @return the request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Sets request.
     *
     * @param request the request
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Gets response.
     *
     * @return the response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets response.
     *
     * @param response the response
     */
    public void setResponse(Response response) {
        this.response = response;
    }
}
