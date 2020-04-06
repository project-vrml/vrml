package com.ten.func.vavr.alert.actor.crash;

import com.ten.func.vavr.alert.actor.AbstractAlertActor;
import com.ten.func.vavr.alert.actor.AlertMessage;
import com.ten.func.vavr.alert.actor.log.LogAlertActor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The crash alert actor.
 */
@Slf4j
@Component
public class CrashAlertActor extends AbstractAlertActor<CrashAlertActor.CrashAlertMessage> {

    /**
     * The crash alert message.
     */
    @Data
    @AllArgsConstructor
    public static class CrashAlertMessage implements AlertMessage {
        private AlertMessage message;
        private Exception exception;
    }

    @Override
    protected void onReceive(CrashAlertMessage message) {
        this.logCrash(message);
    }

    private void logCrash(CrashAlertMessage message) {
        String messageAlert = String.format(
                "[CrashAlertActor] crash happening, see message[%s] with exception[%s]",
                message.getMessage(), message.getException());
        tell(new LogAlertActor.LogAlertMessage(messageAlert));
    }
}