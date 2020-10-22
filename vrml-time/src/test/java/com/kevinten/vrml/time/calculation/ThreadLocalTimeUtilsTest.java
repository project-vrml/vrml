package com.kevinten.vrml.time.calculation;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

/**
 * The Thread local time utils test.
 */
public class ThreadLocalTimeUtilsTest extends TestCase {

    /**
     * Format date.
     */
    @Test
    public void testFormatDate() {
        long currentTimeMillis = System.currentTimeMillis();

        String formatDate1 = ThreadLocalTimeUtils.formatDate(currentTimeMillis);
        String formatDate2 = ThreadLocalTimeUtils.formatDate(new Date(currentTimeMillis));
        String formatDate3 = ThreadLocalTimeUtils.formatDate(new Timestamp(currentTimeMillis));

        assertEquals(formatDate1, formatDate2, formatDate3);
    }

    /**
     * Format time.
     */
    @Test
    public void testFormatTime() {
        long currentTimeMillis = System.currentTimeMillis();

        String formatTime1 = ThreadLocalTimeUtils.formatTime(currentTimeMillis);
        String formatTime2 = ThreadLocalTimeUtils.formatTime(new Date(currentTimeMillis));
        String formatTime3 = ThreadLocalTimeUtils.formatTime(new Timestamp(currentTimeMillis));

        assertEquals(formatTime1, formatTime2, formatTime3);
    }

    /**
     * Format date time.
     */
    @Test
    public void testFormatDateTime() {
        long currentTimeMillis = System.currentTimeMillis();

        String formatDateTime1 = ThreadLocalTimeUtils.formatDateTime(currentTimeMillis);
        String formatDateTime2 = ThreadLocalTimeUtils.formatDateTime(new Date(currentTimeMillis));
        String formatDateTime3 = ThreadLocalTimeUtils.formatDateTime(new Timestamp(currentTimeMillis));

        assertEquals(formatDateTime1, formatDateTime2, formatDateTime3);
    }

    /**
     * Parse date.
     */
    @SneakyThrows
    @Test
    public void testParseDate() {
        // current date time
        long currentTimeMillis = System.currentTimeMillis();

        // current date ignore time
        Date currentDate = new Date(currentTimeMillis);
        String currentDateStr = ThreadLocalTimeUtils.formatDate(currentDate);

        // current date ignore time
        Date currentDateObj = ThreadLocalTimeUtils.parseDate(currentDateStr);
        String currentDateStr2 = ThreadLocalTimeUtils.formatDate(currentDateObj);

        assertEquals(currentDateStr, currentDateStr2);
    }

    /**
     * Parse time.
     */
    @SneakyThrows
    @Test
    public void testParseTime() {
        // current date time
        long currentTimeMillis = System.currentTimeMillis();

        // current time ignore date
        Date currentDate = new Date(currentTimeMillis);
        String currentTimeStr = ThreadLocalTimeUtils.formatTime(currentDate);

        // current time ignore date
        Date currentTimeObj = ThreadLocalTimeUtils.parseTime(currentTimeStr);
        String currentTimeStr2 = ThreadLocalTimeUtils.formatTime(currentTimeObj);

        assertEquals(currentTimeStr, currentTimeStr2);
    }

    /**
     * Parse date time.
     */
    @SneakyThrows
    @Test
    public void testParseDateTime() {
        // current date time
        long currentTimeMillis = System.currentTimeMillis();

        // current date time
        Date currentDateTime = new Date(currentTimeMillis);
        String currentDateTimeStr = ThreadLocalTimeUtils.formatDateTime(currentDateTime);

        // current date time
        Date currentDateTimeObj = ThreadLocalTimeUtils.parseDateTime(currentDateTimeStr);
        String currentDateTimeStr2 = ThreadLocalTimeUtils.formatDateTime(currentDateTimeObj);

        assertEquals(currentDateTimeStr, currentDateTimeStr2);
    }

    /**
     * Current date str.
     */
    @Test
    public void testCurrentDateStr() {
        // current date time
        long currentTimeMillis = System.currentTimeMillis();

        // current date
        String currentDate = ThreadLocalTimeUtils.formatDate(currentTimeMillis);

        // current date
        String currentDateStr = ThreadLocalTimeUtils.currentDateStr();

        assertEquals(currentDate, currentDateStr);
    }

    /**
     * Current time str.
     */
    @Test
    public void testCurrentTimeStr() {
        // current date time
        long currentTimeMillis = System.currentTimeMillis();

        // current time
        String currentTime = ThreadLocalTimeUtils.formatTime(currentTimeMillis);

        // current time
        String currentTimeStr = ThreadLocalTimeUtils.currentTimeStr();

        assertTrue(currentTimeStr.compareTo(currentTime) >= 0);
    }

    /**
     * Current date time str.
     */
    @Test
    public void testCurrentDateTimeStr() {
        // current date time
        long currentTimeMillis = System.currentTimeMillis();

        // current date time
        String currentDateTime = ThreadLocalTimeUtils.formatDateTime(currentTimeMillis);

        // current date time
        String currentDateTimeStr = ThreadLocalTimeUtils.currentDateTimeStr();

        assertTrue(currentDateTimeStr.compareTo(currentDateTime) >= 0);
    }

    /**
     * Current timestamp.
     */
    @Test
    public void testCurrentTimestamp() {
        Timestamp currentTimestamp = ThreadLocalTimeUtils.currentTimestamp();

        // assert not null
        assertNotNull(currentTimestamp);
    }

    /**
     * Current date.
     */
    @Test
    public void testCurrentDate() {
        Date currentDate = ThreadLocalTimeUtils.currentDate();

        // assert not null
        assertNotNull(currentDate);
    }

    /**
     * Test different days.
     */
    @SneakyThrows
    @Test
    public void testDifferentDays() {
        Date currentDate = ThreadLocalTimeUtils.currentDate();

        long same1 = ThreadLocalTimeUtils.differentDays(currentDate, currentDate);
        // assert same is 0
        assertEquals(0, same1);

        Date day1 = ThreadLocalTimeUtils.parseDateTime("2020-01-01 00:00:00");
        Date day2 = ThreadLocalTimeUtils.parseDateTime("2020-01-01 23:59:59");
        long same2 = ThreadLocalTimeUtils.differentDays(day1, day2);
        // assert same is 0
        assertEquals(0, same2);

        Date date1 = ThreadLocalTimeUtils.parseDateTime("2020-01-01 00:00:00");
        Date date2 = ThreadLocalTimeUtils.parseDateTime("2020-01-02 00:00:00");
        long differentDays1 = ThreadLocalTimeUtils.differentDays(date1, date2);
        long differentDays2 = ThreadLocalTimeUtils.differentDays(date2, date1);
        // assert diff is -1
        assertEquals(-1, differentDays1);
        assertEquals(1, differentDays2);

        Date date3 = ThreadLocalTimeUtils.parseDateTime("2020-01-30 00:00:00");
        Date date4 = ThreadLocalTimeUtils.parseDateTime("2020-10-03 00:00:00");
        long differentDays3 = ThreadLocalTimeUtils.differentDays(date3, date4);
        long differentDays4 = ThreadLocalTimeUtils.differentDays(date4, date3);
        // assert diff is -247
        assertEquals(-247, differentDays3);
        assertEquals(247, differentDays4);

        Date year1 = ThreadLocalTimeUtils.parseDateTime("2021-01-30 00:00:00");
        Date year2 = ThreadLocalTimeUtils.parseDateTime("2020-10-03 00:00:00");
        long differentYear1 = ThreadLocalTimeUtils.differentDays(year1, year2);
        long differentYear2 = ThreadLocalTimeUtils.differentDays(year2, year1);
        // assert diff is -119
        assertEquals(119, differentYear1);
        assertEquals(-119, differentYear2);
    }

    /**
     * Gets date format.
     */
    @Test
    public void testGetDateFormat() {
        DateFormat dateFormat1 = ThreadLocalTimeUtils.getDateFormat();
        DateFormat dateFormat2 = ThreadLocalTimeUtils.getDateFormat();

        // assert threadLocal same obj
        assertEquals(dateFormat1, dateFormat2);
    }

    /**
     * Gets time format.
     */
    @Test
    public void testGetTimeFormat() {
        DateFormat timeFormat1 = ThreadLocalTimeUtils.getTimeFormat();
        DateFormat timeFormat2 = ThreadLocalTimeUtils.getTimeFormat();

        // assert threadLocal same obj
        assertEquals(timeFormat1, timeFormat2);
    }

    /**
     * Gets date time format.
     */
    @Test
    public void testGetDateTimeFormat() {
        DateFormat dateTimeFormat1 = ThreadLocalTimeUtils.getDateTimeFormat();
        DateFormat dateTimeFormat2 = ThreadLocalTimeUtils.getDateTimeFormat();

        // assert threadLocal same obj
        assertEquals(dateTimeFormat1, dateTimeFormat2);
    }
}