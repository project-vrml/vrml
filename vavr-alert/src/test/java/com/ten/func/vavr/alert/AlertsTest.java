package com.ten.func.vavr.alert;

import com.ten.func.vavr.alert.actor.email.EmailAlertActor;
import com.ten.func.vavr.alert.actor.log.LogAlertActor;
import com.ten.func.vavr.alert.config.AlertConfiguration;
import com.ten.func.vavr.test.Tests;
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