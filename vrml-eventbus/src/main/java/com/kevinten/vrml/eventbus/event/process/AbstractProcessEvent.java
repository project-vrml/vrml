package com.kevinten.vrml.eventbus.event.process;

import com.kevinten.vrml.eventbus.event.EventBusEvent;
import org.springframework.context.ApplicationEvent;

/**
 * The abstract process event.
 *
 * @param <Request>  the process request type
 * @param <Response> the process response type
 */
public abstract class AbstractProcessEvent<Request, Response> extends ApplicationEvent implements EventBusEvent {

    private final Request request;
    private final Response response;

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
     * Gets response.
     *
     * @return the response
     */
    public Response getResponse() {
        return response;
    }
}
