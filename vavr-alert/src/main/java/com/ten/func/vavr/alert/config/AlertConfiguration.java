package com.ten.func.vavr.alert.config;

import com.ten.func.vavr.alert.actor.AlertMessage;
import com.ten.func.vavr.alert.actor.crash.CrashAlertActor;
import com.ten.func.vavr.alert.actor.email.EmailAlertActor;
import com.ten.func.vavr.alert.actor.log.LogAlertActor;

/**
 * The interface Alert configuration.
 */
public interface AlertConfiguration {

    /**
     * Is alert enable.
     *
     * @param message the message
     * @return {@code true} will process alert
     */
    boolean isAlertEnable(AlertMessage message);

    /**
     * The enum Alert type.
     */
    enum AlertType {

        /**
         * Email alert.
         *
         * @see EmailAlertActor
         */
        EMAIL() {
            @Override
            public AlertMessage createBy(String message) {
                return new EmailAlertActor.EmailAlertMessage(null, message, message);
            }
        },

        /**
         * Log alert.
         *
         * @see LogAlertActor
         */
        LOG {
            @Override
            public AlertMessage createBy(String message) {
                return new LogAlertActor.LogAlertMessage(message);
            }
        },

        /**
         * Crash alert.
         *
         * @see CrashAlertActor
         */
        CRASH {
            @Override
            public AlertMessage createBy(String message) {
                throw new UnsupportedOperationException("can't create crash indirectly");
            }
        },
        ;

        /**
         * Create by alert message.
         *
         * @param message the message
         * @return the alert message
         */
        public abstract AlertMessage createBy(String message);
    }
}
