package io.vrml.alert;

import io.vrml.alert.actor.AbstractAlertActor;
import io.vrml.alert.actor.AlertActorSystem;
import io.vrml.alert.actor.AlertMessage;
import io.vrml.alert.actor.log.DefaultLogAlertActor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The Alerts test.
 */
public class AlertsTest {

    /**
     * Defined your custom {@code alert actor} and register to spring context like {@link Component}.
     * extends {@link AbstractAlertActor} and bind specific alert message type to generic.
     */
    @Component
    public static class TestAlertActor extends AbstractAlertActor<TestAlertActor.TestAlertMessage> {

        /**
         * Defined your custom {@code alert message} which always tied to the actor.
         * impl {@link AlertMessage} so that received by the {@link AlertActorSystem}.
         */
        @Data
        @AllArgsConstructor
        public static class TestAlertMessage implements AlertMessage {
            private final String message;
        }

        /**
         * Receive the {@code alert message} and do your custom alert process.
         */
        @Override
        protected void onReceive(TestAlertMessage message) {
            System.out.println(message.getMessage());
        }
    }

    /**
     * Tell {@code default log alert message} to {@code default log alert actor}.
     * It will logging the alert message by {@link Slf4j} with different log level.
     */
    public void tellDefault() {
        // default log level is ERROR
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with error level"));
        // custom log level inject to constructor
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with error level", DefaultLogAlertActor.AlertsLogLevelType.ERROR));
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with warn level", DefaultLogAlertActor.AlertsLogLevelType.WARN));
    }

    /**
     * Tell {@code custom alert message} to {@code custom alert actor}.
     * It will execute the custom process {@code AbstractAlertActor#onReceive(AlertMessage)}
     */
    public void tellCustom() {
        Alerts.tell(new TestAlertActor.TestAlertMessage("TEST"));
    }
}