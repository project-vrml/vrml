package com.vavr.func.work.alert.actor.log;

import com.vavr.func.work.alert.actor.AbstractAlertActor;
import com.vavr.func.work.alert.actor.AlertMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The log alert actor.
 */
@Slf4j
@Component
public class LogAlertActor extends AbstractAlertActor<LogAlertActor.LogAlertMessage> {

    /**
     * The log alert message.
     */
    @Data
    @AllArgsConstructor
    public static class LogAlertMessage implements AlertMessage {
        private String message;
    }

    /**
     * Log error.
     */
    @Override
    protected void onReceive(LogAlertMessage message) {
        log.error("[LogAlertActor] {}", message.getMessage());
    }
}
