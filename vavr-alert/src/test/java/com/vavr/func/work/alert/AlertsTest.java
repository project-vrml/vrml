package com.vavr.func.work.alert;

import com.vavr.func.work.alert.actor.email.EmailAlertActor;
import com.vavr.func.work.alert.actor.log.LogAlertActor;
import com.vavr.func.work.alert.config.AlertConfiguration;
import com.vavr.func.work.test.Tests;
import org.junit.Test;

/**
 * The alerts test.
 */
class AlertsTest implements Tests {

    /**
     * Test tell.
     */
    @Test
    public void testTell() {
        Alerts.tell("alert test message", AlertConfiguration.AlertType.LOG);
        Alerts.tell("alert test message", AlertConfiguration.AlertType.EMAIL);
    }

    /**
     * Test tell 1.
     */
    @Test
    public void testTell1() {
        Alerts.tell(new LogAlertActor.LogAlertMessage("test message"));
    }

    /**
     * Test tell 2.
     */
    @Test
    public void testTell2() {
        Alerts.tell(
                new LogAlertActor.LogAlertMessage("test message"),
                new EmailAlertActor.EmailAlertMessage(null, "test alert", "test alert content"));
    }
}