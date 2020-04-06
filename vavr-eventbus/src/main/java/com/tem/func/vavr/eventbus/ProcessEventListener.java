package com.tem.func.vavr.eventbus;

import org.springframework.context.event.EventListener;

public class ProcessEventListener extends AbstractProcessEventListener<ProcessEvent> {


    @EventListener
    @Override
    public void onEvent(ProcessEvent event) {

    }
}
