package io.vrml.eventbus.publisher;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Abstract event publisher.
 */
public abstract class AbstractEventPublisher implements ApplicationContextAware {

    /**
     * The Application context.
     */
    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
