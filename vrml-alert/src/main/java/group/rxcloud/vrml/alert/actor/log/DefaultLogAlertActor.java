package group.rxcloud.vrml.alert.actor.log;

import group.rxcloud.vrml.alert.actor.AbstractAlertActor;
import group.rxcloud.vrml.alert.actor.AlertMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The default log alert actor.
 */
@Slf4j
@Component
public class DefaultLogAlertActor extends AbstractAlertActor<DefaultLogAlertActor.DefaultLogAlertMessage> {

    /**
     * The default log alert message.
     */
    @Data
    public static class DefaultLogAlertMessage implements AlertMessage {

        private final String message;
        private AlertsLogLevelType logLevel;

        /**
         * Instantiates a new default log alert message with {@code ERROR} level.
         *
         * @param message the message
         */
        public DefaultLogAlertMessage(String message) {
            this.message = message;
            this.initLevel();
        }

        /**
         * Instantiates a new default log alert message.
         *
         * @param message  the message
         * @param logLevel the log level
         */
        public DefaultLogAlertMessage(String message, AlertsLogLevelType logLevel) {
            this.message = message;
            this.logLevel = logLevel;
            this.initLevel();
        }

        private void initLevel() {
            if (this.logLevel == null) {
                this.logLevel = AlertsLogLevelType.ERROR;
            }
        }
    }

    /**
     * The Alerts log level type.
     */
    public enum AlertsLogLevelType {

        /**
         * Log debug.
         */
        DEBUG {
            @Override
            protected void log(String message) {
                log.debug(ALERTS_PREFIX, message);
            }
        },

        /**
         * Log info.
         */
        INFO {
            @Override
            protected void log(String message) {
                log.info(ALERTS_PREFIX, message);
            }
        },

        /**
         * Log warn.
         */
        WARN {
            @Override
            protected void log(String message) {
                log.warn(ALERTS_PREFIX, message);
            }
        },

        /**
         * Log error.
         */
        ERROR {
            @Override
            protected void log(String message) {
                log.error(ALERTS_PREFIX, message);
            }
        },
        ;

        protected static final String ALERTS_PREFIX = "[Vrml][Alerts] {}";

        /**
         * Logging.
         *
         * @param message the message
         */
        protected abstract void log(String message);
    }

    @Override
    protected void onReceive(DefaultLogAlertMessage message) {
        if (message != null) {
            message.getLogLevel().log(message.getMessage());
        }
    }
}
