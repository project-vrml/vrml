package group.rxcloud.vrml.eventbus.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * The Abstract event listener.
 */
@Slf4j
public abstract class AbstractEventListener implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        log.info("[Vrml][{}] prepared.", this.getClass().getSimpleName());
    }

    /*
     * Use {@code @org.springframework.context.event.EventListener} listener for events of interest.
     */
}
