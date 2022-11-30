package group.rxcloud.vrml.eventbus.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * The Abstract event listener.
 */
public abstract class AbstractEventListener implements ApplicationListener<ApplicationPreparedEvent> {

    private static final Logger log = LoggerFactory.getLogger(AbstractEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        log.info("[Vrml][{}] prepared.", this.getClass().getSimpleName());
    }

    /*
     * Use {@code @org.springframework.context.event.EventListener} listener for events of interest.
     */
}
