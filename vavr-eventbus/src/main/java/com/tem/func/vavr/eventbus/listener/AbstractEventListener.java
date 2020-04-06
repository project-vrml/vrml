package com.tem.func.vavr.eventbus.listener;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * The abstract event listener.
 */
@Slf4j
public abstract class AbstractEventListener implements ApplicationListener<ApplicationPreparedEvent> {

    /**
     * The tool to convert data.
     */
    protected static final Gson GSON = new Gson();

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        log.info("[AbstractEventListener] [{}] prepared.", this.getClass().getSimpleName());
    }
}
