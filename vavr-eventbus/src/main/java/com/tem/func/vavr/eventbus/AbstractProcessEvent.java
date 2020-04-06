package com.tem.func.vavr.eventbus;

import org.springframework.context.ApplicationEvent;

public abstract class AbstractProcessEvent<Request, Response> extends ApplicationEvent {

    private Request request;
    private Response response;

    public AbstractProcessEvent(Object source, Request request, Response response) {
        super(source);
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
