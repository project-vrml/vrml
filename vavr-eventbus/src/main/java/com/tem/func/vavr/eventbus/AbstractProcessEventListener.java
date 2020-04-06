package com.tem.func.vavr.eventbus;

public abstract class AbstractProcessEventListener<Event extends AbstractProcessEvent> {

    public abstract void onEvent(Event event);

}
