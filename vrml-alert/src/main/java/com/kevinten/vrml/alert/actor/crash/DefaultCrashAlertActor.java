package com.kevinten.vrml.alert.actor.crash;

import com.kevinten.vrml.alert.actor.AbstractAlertActor;
import com.kevinten.vrml.alert.actor.AlertMessage;
import com.kevinten.vrml.alert.actor.log.DefaultLogAlertActor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * The default crash alert actor.
 */
@Slf4j
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
                "[Alerts.DefaultCrashAlertActor.onReceive] crash happening, see message[%s] with exception[%s]",
                message.getMessage(), message.getException());
        tell(new DefaultLogAlertActor.DefaultLogAlertMessage(messageAlert));
    }
}