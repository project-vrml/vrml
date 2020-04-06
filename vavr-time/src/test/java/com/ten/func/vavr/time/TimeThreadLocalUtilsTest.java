package com.ten.func.vavr.time;

import com.ten.func.vavr.test.Tests;
import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

/**
 * {@link TimeThreadLocalUtils} API test.
 */
public class TimeThreadLocalUtilsTest implements Tests {

    /**
     * Format date time.
     */
    @Test
    public void formatDateTime() {
        TimeThreadLocalUtils.formatDateTime(new Date());
    }

    /**
     * Test format date time.
     */
    @Test
    public void testFormatDateTime() {
        TimeThreadLocalUtils.formatDateTime(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Test format date time 1.
     */
    @Test
    public void testFormatDateTime1() {
        TimeThreadLocalUtils.formatDateTime(System.currentTimeMillis());
    }

    /**
     * Format date.
     */
    @Test
    public void formatDate() {
        TimeThreadLocalUtils.formatDate(new Date());
    }

    /**
     * Test format date.
     */
    @Test
    public void testFormatDate() {
        TimeThreadLocalUtils.formatDate(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Format time.
     */
    @Test
    public void formatTime() {
        TimeThreadLocalUtils.formatTime(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Parse date time.
     */
    @SneakyThrows
    @Test
    public void parseDateTime() {
        TimeThreadLocalUtils.parseDateTime(TimeThreadLocalUtils.formatDateTime(new Timestamp(System.currentTimeMillis())));
    }

    /**
     * Parse date.
     */
    @SneakyThrows
    @Test
    public void parseDate() {
        TimeThreadLocalUtils.parseDate(TimeThreadLocalUtils.formatDateTime(new Timestamp(System.currentTimeMillis())));
    }

    /**
     * Current date time.
     */
    @Test
    public void currentDateTime() {
        TimeThreadLocalUtils.currentDateTime();
    }

    /**
     * Current timestamp.
     */
    @Test
    public void currentTimestamp() {
        TimeThreadLocalUtils.currentTimestamp();
    }
}