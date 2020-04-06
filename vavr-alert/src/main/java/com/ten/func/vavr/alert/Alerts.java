package com.ten.func.vavr.alert;

import com.ten.func.vavr.alert.actor.AlertActorSystem;
import com.ten.func.vavr.alert.actor.AlertMessage;
import com.ten.func.vavr.alert.config.AlertConfiguration;
import com.ten.func.vavr.core.tags.TodoUp;
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
