package com.kevinten.vrml.time.calculation;

import com.google.common.base.Preconditions;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * The {@code ThreadLocal} time utils.
 * Avoid the overhead of creating {@code DateFormat} repeatedly.
 * Focus on the three types of Date/Time/DateTime.
 * Operation orientation to the {@code Date}/{@code Timestamp}/{@code String}.
 */
@ThreadSafe
public abstract class ThreadLocalTimeUtils extends DateUtils {

    private ThreadLocalTimeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * The constant PREFIX_START.
     */
    protected static final int PREFIX_START = 0;

    /**
     * "Date" format properties
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int DATE_FORMAT_LENGTH = DATE_FORMAT.length();
    protected static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * "Time" format properties
     */
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final int TIME_FORMAT_LENGTH = TIME_FORMAT.length();
    protected static final ThreadLocal<DateFormat> TIME_FORMAT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * "Date Time" format properties
     */
    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    public static final int DATE_TIME_FORMAT_LENGTH = DATE_TIME_FORMAT.length();
    protected static final ThreadLocal<DateFormat> DATE_TIME_FORMAT_THREAD_LOCAL = new ThreadLocal<>();

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            DATE_FORMAT_THREAD_LOCAL.remove();
            TIME_FORMAT_THREAD_LOCAL.remove();
            DATE_TIME_FORMAT_THREAD_LOCAL.remove();
        });
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Date/Time Format.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Format {@code TimeMillis} to date string.
     *
     * @param timeMillis the {@code TimeMillis}
     * @return the date string
     */
    public static String formatDate(long timeMillis) {
        String result = getDateFormat().format(timeMillis);
        Preconditions.checkArgument(result.length() == DATE_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code Date} to date string.
     *
     * @param date the {@code Date}
     * @return the date string
     */
    public static String formatDate(Date date) {
        String result = getDateFormat().format(date);
        Preconditions.checkArgument(result.length() == DATE_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code Timestamp} to date string.
     *
     * @param timestamp the {@code Timestamp}
     * @return the date string
     */
    public static String formatDate(Timestamp timestamp) {
        String result = getDateFormat().format(timestamp);
        Preconditions.checkArgument(result.length() == DATE_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code TimeMillis} to time string.
     *
     * @param timeMillis the {@code TimeMillis}
     * @return the time string
     */
    public static String formatTime(long timeMillis) {
        String result = getTimeFormat().format(timeMillis);
        Preconditions.checkArgument(result.length() == TIME_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code Date} to time string.
     *
     * @param date the {@code Date}
     * @return the time string
     */
    public static String formatTime(Date date) {
        String result = getTimeFormat().format(date);
        Preconditions.checkArgument(result.length() == TIME_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code Timestamp} to time string.
     *
     * @param timestamp the {@code Timestamp}
     * @return the time string
     */
    public static String formatTime(Timestamp timestamp) {
        String result = getTimeFormat().format(timestamp);
        Preconditions.checkArgument(result.length() == TIME_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code TimeMillis} to date time string.
     *
     * @param timeMillis the {@code TimeMillis}
     * @return the date time string
     */
    public static String formatDateTime(long timeMillis) {
        String result = getDateTimeFormat().format(timeMillis);
        Preconditions.checkArgument(result.length() == DATE_TIME_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code Date} to date time string.
     *
     * @param date the {@code Date}
     * @return the date time string
     */
    public static String formatDateTime(Date date) {
        String result = getDateTimeFormat().format(date);
        Preconditions.checkArgument(result.length() == DATE_TIME_FORMAT_LENGTH);
        return result;
    }

    /**
     * Format {@code Timestamp} to date time string.
     *
     * @param timestamp the {@code Timestamp}
     * @return the date time string
     */
    public static String formatDateTime(Timestamp timestamp) {
        String result = getDateTimeFormat().format(timestamp);
        Preconditions.checkArgument(result.length() == DATE_TIME_FORMAT_LENGTH);
        return result;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Date/Time Parse.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Parse {@code Date} from date string.
     *
     * @param strDate the str date
     * @return the {@code Date}
     * @throws ParseException the parse exception
     */
    public static Date parseDate(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    /**
     * Parse {@code Date} from time string.
     *
     * @param strDate the str time
     * @return the {@code Date}
     * @throws ParseException the parse exception
     */
    public static Date parseTime(String strDate) throws ParseException {
        return getTimeFormat().parse(strDate);
    }

    /**
     * Parse {@code Date} from date time string.
     *
     * @param strDate the str date time
     * @return the {@code Date}
     * @throws ParseException the parse exception
     */
    public static Date parseDateTime(String strDate) throws ParseException {
        return getDateTimeFormat().parse(strDate);
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Date/Timestamp Convert.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Convert date.
     *
     * @param timestamp the timestamp
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date convert(Timestamp timestamp) throws ParseException {
        String formatDateTime = formatDateTime(timestamp);
        return parseDateTime(formatDateTime);
    }

    /**
     * Convert timestamp.
     *
     * @param date the date
     * @return the timestamp
     * @throws ParseException the parse exception
     */
    public static Timestamp convert(Date date) throws ParseException {
        String formatDateTime = formatDateTime(date);
        return new Timestamp(parseDateTime(formatDateTime).getTime());
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Date/Time Values.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Current date string.
     *
     * @return {@link #DATE_FORMAT}
     */
    public static String currentDateStr() {
        String dateTime = currentTimestamp().toString();
        return dateTime.substring(PREFIX_START, DATE_FORMAT_LENGTH);
    }

    /**
     * Current time string.
     *
     * @return {@link #TIME_FORMAT}
     */
    public static String currentTimeStr() {
        String dateTime = currentTimestamp().toString();
        return dateTime.substring(DATE_FORMAT_LENGTH + 1, DATE_TIME_FORMAT_LENGTH);
    }

    /**
     * Current date time string.
     *
     * @return {@link #DATE_TIME_FORMAT}
     */
    public static String currentDateTimeStr() {
        String dateTime = currentTimestamp().toString();
        return dateTime.substring(PREFIX_START, DATE_TIME_FORMAT_LENGTH);
    }

    /**
     * Current {@code Timestamp}.
     *
     * @return the timestamp
     */
    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Current {@code Date}.
     *
     * @return the date
     */
    public static Date currentDate() {
        return new Date();
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Date/Time Calculation.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Calculate different days more A than B
     *
     * @param A the a
     * @param B the b
     * @return (long) (A - B)
     * @apiNote This days code is counting days based on {@code UTC}. The days elapsed may be a different count if considered with the time zone of the original Calendar objects. Better approach would convert using GregorianCalendar to ZonedDateTime, extract LocalDate, and calculate Period.
     */
    public static long differentDays(java.util.Date A, java.util.Date B) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(A);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(B);
        return ChronoUnit.DAYS.between(cal2.toInstant(), cal1.toInstant());
    }

    // -- PRIVATE API

    /**
     * Gets date format.
     *
     * @return the date format
     * @apiNote ThreadSafe
     */
    protected static DateFormat getDateFormat() {
        DateFormat df = DATE_FORMAT_THREAD_LOCAL.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_FORMAT);
            DATE_FORMAT_THREAD_LOCAL.set(df);
        }
        return df;
    }

    /**
     * Gets time format.
     *
     * @return the time format
     * @apiNote ThreadSafe
     */
    protected static DateFormat getTimeFormat() {
        DateFormat df = TIME_FORMAT_THREAD_LOCAL.get();
        if (df == null) {
            df = new SimpleDateFormat(TIME_FORMAT);
            TIME_FORMAT_THREAD_LOCAL.set(df);
        }
        return df;
    }

    /**
     * Gets date time format.
     *
     * @return the date time format
     * @apiNote ThreadSafe
     */
    protected static DateFormat getDateTimeFormat() {
        DateFormat df = DATE_TIME_FORMAT_THREAD_LOCAL.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_FORMAT_THREAD_LOCAL.set(df);
        }
        return df;
    }
}
