package com.ten.func.vavr.time;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Time formatter using {@code ThreadLocal}
 */
@Slf4j
public abstract class TimeThreadLocalUtils {

    /**
     * "Date" format properties
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int DATE_FORMAT_LENGTH = DATE_FORMAT.length();

    private static ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<>();

    /**
     * "Date Time" format properties
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int DATE_TIME_FORMAT_LENGTH = DATE_TIME_FORMAT.length();

    private static ThreadLocal<DateFormat> dateTimeFormatThreadLocal = new ThreadLocal<>();

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            dateFormatThreadLocal.remove();
            dateTimeFormatThreadLocal.remove();
        });
    }

    /*
     Format : to String
    */

    // -- Date Time

    /**
     * Format date time string.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDateTime(Date date) {
        String result = getDateTimeFormat().format(date);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse datetime: " + date + ", the result is " + result);
        }
        return result;
    }

    /**
     * Format date time string.
     *
     * @param datetime the datetime
     * @return the string
     */
    public static String formatDateTime(long datetime) {
        String result = getDateTimeFormat().format(datetime);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse datetime: " + datetime + ", the result is " + result);
        }
        return result;
    }

    /**
     * Format date time string.
     *
     * @param timestamp the timestamp
     * @return the string
     */
    public static String formatDateTime(Timestamp timestamp) {
        String result = getDateTimeFormat().format(timestamp);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse timestamp: " + timestamp + ", the result is " + result);
        }
        return result;
    }

    // -- Date

    /**
     * Format date string.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDate(Date date) {
        String result = getDateFormat().format(date);
        if (result.length() != DATE_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse date: " + date + ", the result is " + result);
        }
        return result;
    }

    /**
     * Format date string.
     *
     * @param timestamp the timestamp
     * @return the string
     */
    public static String formatDate(Timestamp timestamp) {
        String result = getDateFormat().format(timestamp);
        if (result.length() != DATE_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse timestamp: " + timestamp + ", the result is " + result);
        }
        return result;
    }

    // -- Time

    /**
     * Format time string.
     *
     * @param timestamp the timestamp
     * @return the string
     */
    public static String formatTime(Timestamp timestamp) {
        String result = getDateTimeFormat().format(timestamp);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse time: " + timestamp + ", the result is " + result);
        }
        return result.split(" ")[1];
    }

    /*
     Parse : to Date
    */

    // -- Date Time

    /**
     * Parse date time date.
     *
     * @param strDateTime the str date time
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date parseDateTime(String strDateTime) throws ParseException {
        return getDateTimeFormat().parse(strDateTime);
    }

    // -- Date

    /**
     * Parse date date.
     *
     * @param strDate the str date
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date parseDate(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    /*
     Value : get time value
    */

    /**
     * Current date time string.
     *
     * @return the string
     */
    public static String currentDateTime() {
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        String dateTime = timestamp.toString();
        return dateTime.substring(0, DATE_TIME_FORMAT_LENGTH);
    }

    /**
     * Current timestamp timestamp.
     *
     * @return the timestamp
     */
    public static Timestamp currentTimestamp() {
        long currentTimeMillis = System.currentTimeMillis();
        return new Timestamp(currentTimeMillis);
    }

    // -------------------------------------------------------------- private

    private static DateFormat getDateFormat() {
        DateFormat df = dateFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_FORMAT);
            dateFormatThreadLocal.set(df);
        }
        return df;
    }

    private static DateFormat getDateTimeFormat() {
        DateFormat df = dateTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_TIME_FORMAT);
            dateTimeFormatThreadLocal.set(df);
        }
        return df;
    }
}
