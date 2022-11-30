package group.rxcloud.vrml.alert.actor.crash;

import group.rxcloud.vrml.alert.actor.AbstractAlertActor;
import group.rxcloud.vrml.alert.actor.AlertMessage;
import group.rxcloud.vrml.alert.actor.log.DefaultLogAlertActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The default crash alert actor.
 */
@Component
public class DefaultCrashAlertActor extends AbstractAlertActor<DefaultCrashAlertActor.DefaultCrashAlertMessage> {

    private static final Logger log = LoggerFactory.getLogger(DefaultCrashAlertActor.class);

    /**
     * The default crash alert message.
     */
    public static class DefaultCrashAlertMessage implements AlertMessage {

        private final AlertMessage message;
        private final Exception exception;

        public AlertMessage getMessage() {
            return message;
        }

        public Exception getException() {
            return exception;
        }

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