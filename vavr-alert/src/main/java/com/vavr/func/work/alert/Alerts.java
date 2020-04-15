package com.vavr.func.work.alert;

import com.vavr.func.work.alert.actor.AlertActorSystem;
import com.vavr.func.work.alert.actor.AlertMessage;
import com.vavr.func.work.alert.config.AlertConfiguration;
import com.vavr.func.work.core.tags.TodoUp;
import lombok.extern.slf4j.Slf4j;

/**
 * The Alerts API.
 */
@Slf4j
public final class Alerts {

    /**
     * Tell async message.
     *
     * @param message the immutable message
     */
    public static void tell(String message, AlertConfiguration.AlertType... alertTypes) {
        for (final AlertConfiguration.AlertType alertType : alertTypes) {
            final AlertMessage alertMessage = alertType.createBy(message);
            AlertActorSystem.tell(alertMessage);
        }
    }

    /**
     * Tell async message.
     *
     * @param message the immutable message
     */
    @TodoUp(optimizeDesc = "make message immutable")
    public static void tell(final AlertMessage message) {
        AlertActorSystem.tell(message);
    }

    /**
     * Tell async message.
     *
     * @param messages the immutable message
     */
    @TodoUp(optimizeDesc = "make message immutable")
    public static void tell(final AlertMessage... messages) {
        for (final AlertMessage message : messages) {
            AlertActorSystem.tell(message);
        }
    }
}
