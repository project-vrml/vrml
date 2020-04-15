package com.vavr.func.work.log;

import com.vavr.func.work.test.Tests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Logs} API test.
 */
@Slf4j
public class LogsTest implements Tests {

    /**
     * Test create
     */
    @Test
    public void getLogs1() {
        Logs logs = Logs.Factory.getLogs(LogsTest.class);
        logs.info("info");
        logs.key("debug-hello").info("debug-hello-info");
    }

    /**
     * Test create
     */
    @Test
    public void getLogs2() {
        Logs logs = Logs.Factory.getLogs(log);
        logs.info("info");
        logs.key("debug-hello").info("debug-hello-info");
    }

    @Test
    public void key() {
        // the same logs when init key
        Logs logs1 = Logs.Factory.getLogs(log);
        Logs key1 = logs1.key(LogsTest.class);
        Assert.assertEquals(logs1, key1);

        // the same logs when same key
        Logs logs2 = Logs.Factory.getLogs(log).key(LogsTest.class);
        Logs key2 = logs2.key(LogsTest.class);
        Assert.assertEquals(logs2, key2);

        // the diff logs when diff key
        Logs logs3 = Logs.Factory.getLogs(log).key(LogsTest.class);
        Logs key3 = logs3.key("default");
        Assert.assertNotEquals(logs3, key3);
    }
}