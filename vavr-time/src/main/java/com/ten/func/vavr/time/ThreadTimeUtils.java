package com.ten.func.vavr.time;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Time formatter using ThreadLocal
 *
 * @author shihaowang
 * @date 2019/8/28
 */
@Slf4j
public class ThreadTimeUtils {

    /**
     * Date
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int DATE_FORMAT_LENGTH = 10;

    /**
     * Data Time
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int DATE_TIME_FORMAT_LENGTH = 19;

    private static ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<DateFormat> dateTimeFormatThreadLocal = new ThreadLocal<>();

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            dateFormatThreadLocal.remove();
            dateTimeFormatThreadLocal.remove();
        });
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        String result = getDateTimeFormat().format(date);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse Date " + date + ", the result is " + result);
        }
        return result;
    }

    public static String formatDateTime(long datetime) {
        String result = getDateTimeFormat().format(datetime);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse Date " + datetime + ", the result is " + result);
        }
        return result;
    }

    public static String formatDateTime(Timestamp datetime) {
        if (datetime == null) {
            return null;
        }
        String result = getDateTimeFormat().format(datetime);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse Date " + datetime + ", the result is " + result);
        }
        return result;
    }

    public static String formatDate(Timestamp datetime) {
        if (datetime == null) {
            return null;
        }
        String result = getDateFormat().format(datetime);
        if (result.length() != DATE_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse Date " + datetime + ", the result is " + result);
        }
        return result;
    }

    public static String formatTime(Timestamp datetime) {
        if (datetime == null) {
            return null;
        }
        String result = getDateTimeFormat().format(datetime);
        if (result.length() != DATE_TIME_FORMAT_LENGTH) {
            throw new IllegalArgumentException("When parse Date " + datetime + ", the result is " + result);
        }
        return result.split(" ")[1];
    }

    public static Date parseDate(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    public static Date parseDateTime(String strDateTime) throws ParseException {
        return getDateTimeFormat().parse(strDateTime);
    }

    public static String currentDateTime() {
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        String dateTime = timestamp.toString();
        return dateTime.substring(0, DATE_TIME_FORMAT_LENGTH);
    }

    public static Timestamp currentTimestamp() {
        long currentTimeMillis = System.currentTimeMillis();
        return new Timestamp(currentTimeMillis);
    }

    public static Date safeFormat(Timestamp timestamp) {
        String formatDateTime = formatDateTime(timestamp);
        try {
            return parseDateTime(formatDateTime);
        } catch (ParseException e) {
            log.error("TimeParse exception", e);
            return null;
        }
    }

    /**
     * to比from多的天数
     *
     * @param from 被比较日期
     * @param to   比较日起
     * @return to-from
     */
    public static int differentDays(Date from, Date to) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(from);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(to);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        // 不同年
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                // 闰年
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    timeDistance += 366;
                }
                // 不是闰年
                else {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        }
        // 同一年
        else {
            return day2 - day1;
        }
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

    public static void main(String[] args) {
        String dateStr = "2019-10-30 1:21:28";
        String dateStr2 = "2019-10-31 1:21:28";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date2 = format.parse(dateStr2);
            Date date = format.parse(dateStr);

            System.out.println("The difference between the two dates: " + differentDays(date, date2));
        } catch (ParseException e) {
            log.error("TimeParse exception", e);
        }
    }
}
