package group.rxcloud.vrml.alert.actor.crash;

import group.rxcloud.vrml.alert.actor.AbstractAlertActor;
import group.rxcloud.vrml.alert.actor.AlertMessage;
import group.rxcloud.vrml.alert.actor.log.DefaultLogAlertActor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The default crash alert actor.
 */
@Slf4j
@Component
public class DefaultCrashAlertActor extends AbstractAlertActor<DefaultCrashAlertActor.DefaultCrashAlertMessage> {

    /**
     * The default crash alert message.
     */
    @Data
    public static class DefaultCrashAlertMessage implements AlertMessage {

        private final AlertMessage message;
        private final Exception exception;

        public DefaultCrashAlertMessage(AlertMessage message, Exception exception) {
            this.message = message;
            this.exception = exception;
        }
    }

    @Override
    protected void onReceive(DefaultCrashAlertMessage message) {
        String messageAlert = String.format(
                "[Vrml][Alerts.DefaultCrashAlertActor.onReceive] crash happening, see message[%s] with exception[%s]",
                message.getMessage(), message.getException());
        tell(new DefaultLogAlertActor.DefaultLogAlertMessage(messageAlert));
    }
}