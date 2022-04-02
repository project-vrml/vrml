package group.rxcloud.vrml.time.cron;

import group.rxcloud.vrml.time.calculation.ThreadLocalTimeUtils;
import group.rxcloud.vrml.time.timezone.TimeZoneEnum;
import group.rxcloud.vrml.time.timezone.TimeZoneUtils;
import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * The Cron expression utils test.
 */
public class CronExpressionUtilsTest extends TestCase {

    /**
     * Parse cron with time zone.
     */
    @Test
    public void testParseCronWithTimeZone() {
        // current time
        Timestamp currentTimestamp = ThreadLocalTimeUtils.currentTimestamp();

        // test parse cron
        testParseCronWithTimeZoneEveryDay(currentTimestamp);
        testParseCronWithTimeZoneEveryWeek(currentTimestamp);
        testParseCronWithTimeZoneEveryMonth(currentTimestamp);
    }

    /**
     * EVERY_DAY: x x x * * ?
     */
    @SneakyThrows
    public void testParseCronWithTimeZoneEveryDay(Timestamp currentTimestamp) {
        CronExpressionUtils.TimeZoneCronParseBuilder builder = new CronExpressionUtils.TimeZoneCronParseBuilder();
        builder.setCronModeEnum(CronModeEnum.EVERY_DAY);
        builder.setSourceExecuteTime(currentTimestamp);

        for (TimeZoneEnum sourceTimeZone : TimeZoneEnum.listTimeZones()) {

            for (TimeZoneEnum targetTimeZone : TimeZoneEnum.listTimeZones()) {

                builder.setSourceTimeZone(sourceTimeZone);
                builder.setTargetTimeZone(targetTimeZone);

                // timezone cron
                String cron = CronExpressionUtils.parseCronToTargetTimeZone(builder);
                System.out.println(String.format("day source[%s] target[%s] cron[%s]", sourceTimeZone.getValue(), targetTimeZone.getValue(), cron));
                String[] cronItems = cron.split(" ");

                // target time
                Timestamp targetTimestamp = TimeZoneUtils.parseTimezoneTimestamp(sourceTimeZone, targetTimeZone, currentTimestamp);
                String targetTime = ThreadLocalTimeUtils.formatTime(targetTimestamp);
                String[] timeItems = targetTime.split(":");

                // assert time equals
                assertEquals(Integer.parseInt(timeItems[0]), Integer.parseInt(cronItems[2]));
                assertEquals(Integer.parseInt(timeItems[1]), Integer.parseInt(cronItems[1]));
                assertEquals(Integer.parseInt(timeItems[2]), Integer.parseInt(cronItems[0]));
                // assert every day
                assertEquals("*", cronItems[3]);
                assertEquals("*", cronItems[4]);
                assertEquals("?", cronItems[5]);
            }
        }
    }

    /**
     * EVERY_WEEK: x x x ? * week+1
     */
    @SneakyThrows
    public void testParseCronWithTimeZoneEveryWeek(Timestamp currentTimestamp) {
        CronExpressionUtils.TimeZoneCronParseBuilder builder = new CronExpressionUtils.TimeZoneCronParseBuilder();
        builder.setCronModeEnum(CronModeEnum.EVERY_WEEK);
        builder.setDayOfWeek(1);
        builder.setSourceExecuteTime(currentTimestamp);

        for (TimeZoneEnum sourceTimeZone : TimeZoneEnum.listTimeZones()) {

            for (TimeZoneEnum targetTimeZone : TimeZoneEnum.listTimeZones()) {

                builder.setSourceTimeZone(sourceTimeZone);
                builder.setTargetTimeZone(targetTimeZone);

                // timezone cron
                String cron = CronExpressionUtils.parseCronToTargetTimeZone(builder);
                System.out.println(String.format("week source[%s] target[%s] cron[%s]", sourceTimeZone.getValue(), targetTimeZone.getValue(), cron));
                String[] cronItems = cron.split(" ");

                // target time
                Timestamp targetTimestamp = TimeZoneUtils.parseTimezoneTimestamp(sourceTimeZone, targetTimeZone, currentTimestamp);
                String targetTime = ThreadLocalTimeUtils.formatTime(targetTimestamp);
                String[] timeItems = targetTime.split(":");

                // assert time equals
                assertEquals(Integer.parseInt(timeItems[0]), Integer.parseInt(cronItems[2]));
                assertEquals(Integer.parseInt(timeItems[1]), Integer.parseInt(cronItems[1]));
                assertEquals(Integer.parseInt(timeItems[2]), Integer.parseInt(cronItems[0]));
                // assert every week
                assertEquals("?", cronItems[3]);
                assertEquals("*", cronItems[4]);

                // assert week diff
                String sourceDate = ThreadLocalTimeUtils.formatDate(currentTimestamp);
                String targetDate = ThreadLocalTimeUtils.formatDate(targetTimestamp);
                if (sourceDate.equals(targetDate)) {
                    assertEquals("2", cronItems[5]);
                } else {
                    assertNotSame("2", cronItems[5]);
                }
            }
        }
    }

    /**
     * EVERY_MONTH: x x x month * ?
     */
    @SneakyThrows
    public void testParseCronWithTimeZoneEveryMonth(Timestamp currentTimestamp) {
        CronExpressionUtils.TimeZoneCronParseBuilder builder = new CronExpressionUtils.TimeZoneCronParseBuilder();
        builder.setCronModeEnum(CronModeEnum.EVERY_MONTH);
        builder.setDayOfMonth(1);
        builder.setSourceExecuteTime(currentTimestamp);

        for (TimeZoneEnum sourceTimeZone : TimeZoneEnum.listTimeZones()) {

            for (TimeZoneEnum targetTimeZone : TimeZoneEnum.listTimeZones()) {

                builder.setSourceTimeZone(sourceTimeZone);
                builder.setTargetTimeZone(targetTimeZone);

                // timezone cron
                String cron = CronExpressionUtils.parseCronToTargetTimeZone(builder);
                System.out.println(String.format("month source[%s] target[%s] cron[%s]", sourceTimeZone.getValue(), targetTimeZone.getValue(), cron));
                String[] cronItems = cron.split(" ");

                // target time
                Timestamp targetTimestamp = TimeZoneUtils.parseTimezoneTimestamp(sourceTimeZone, targetTimeZone, currentTimestamp);
                String targetTime = ThreadLocalTimeUtils.formatTime(targetTimestamp);
                String[] timeItems = targetTime.split(":");

                // assert time equals
                assertEquals(Integer.parseInt(timeItems[0]), Integer.parseInt(cronItems[2]));
                assertEquals(Integer.parseInt(timeItems[1]), Integer.parseInt(cronItems[1]));
                assertEquals(Integer.parseInt(timeItems[2]), Integer.parseInt(cronItems[0]));

                // assert month diff
                String sourceDate = ThreadLocalTimeUtils.formatDate(currentTimestamp);
                String targetDate = ThreadLocalTimeUtils.formatDate(targetTimestamp);
                if (sourceDate.equals(targetDate)) {
                    assertEquals("1", cronItems[3]);
                } else {
                    assertNotSame("1", cronItems[3]);
                }

                // assert every month
                assertEquals("*", cronItems[4]);
                assertEquals("?", cronItems[5]);
            }
        }
    }

    /**
     * Parse cron.
     */
    @Test
    public void testParseCron() {
        // current time
        Timestamp currentTimestamp = ThreadLocalTimeUtils.currentTimestamp();
        String time = ThreadLocalTimeUtils.formatTime(currentTimestamp);
        String[] timeItems = time.split(":");

        // test parse cron
        testParseCronEveryDay(currentTimestamp, timeItems);
        testParseCronEveryWeek(currentTimestamp, timeItems);
        testParseCronEveryMonth(currentTimestamp, timeItems);
    }

    /**
     * EVERY_DAY: x x x * * ?
     */
    public void testParseCronEveryDay(Timestamp currentTimestamp, String[] timeItems) {
        CronExpressionUtils.SimpleCronParseBuilder builder = new CronExpressionUtils.SimpleCronParseBuilder();
        builder.setCronModeEnum(CronModeEnum.EVERY_DAY);
        builder.setExecuteTime(currentTimestamp);

        String cron = CronExpressionUtils.parseCron(builder);
        System.out.println("day cron:" + cron);
        String[] cronItems = cron.split(" ");

        // assert time equals
        assertEquals(Integer.parseInt(timeItems[0]), Integer.parseInt(cronItems[2]));
        assertEquals(Integer.parseInt(timeItems[1]), Integer.parseInt(cronItems[1]));
        assertEquals(Integer.parseInt(timeItems[2]), Integer.parseInt(cronItems[0]));
        // assert every day
        assertEquals("*", cronItems[3]);
        assertEquals("*", cronItems[4]);
        assertEquals("?", cronItems[5]);
    }

    /**
     * EVERY_WEEK: x x x ? * week+1
     */
    public void testParseCronEveryWeek(Timestamp currentTimestamp, String[] timeItems) {
        CronExpressionUtils.SimpleCronParseBuilder builder = new CronExpressionUtils.SimpleCronParseBuilder();
        builder.setCronModeEnum(CronModeEnum.EVERY_WEEK);
        builder.setDayOfWeek(1);
        builder.setExecuteTime(currentTimestamp);

        String cron = CronExpressionUtils.parseCron(builder);
        System.out.println("week cron:" + cron);
        String[] cronItems = cron.split(" ");

        // assert time equals
        assertEquals(Integer.parseInt(timeItems[0]), Integer.parseInt(cronItems[2]));
        assertEquals(Integer.parseInt(timeItems[1]), Integer.parseInt(cronItems[1]));
        assertEquals(Integer.parseInt(timeItems[2]), Integer.parseInt(cronItems[0]));
        // assert every week
        assertEquals("?", cronItems[3]);
        assertEquals("*", cronItems[4]);
        assertEquals("2", cronItems[5]);
    }

    /**
     * EVERY_MONTH: x x x month * ?
     */
    public void testParseCronEveryMonth(Timestamp currentTimestamp, String[] timeItems) {
        CronExpressionUtils.SimpleCronParseBuilder builder = new CronExpressionUtils.SimpleCronParseBuilder();
        builder.setCronModeEnum(CronModeEnum.EVERY_MONTH);
        builder.setDayOfMonth(1);
        builder.setExecuteTime(currentTimestamp);

        String cron = CronExpressionUtils.parseCron(builder);
        System.out.println("month cron:" + cron);
        String[] cronItems = cron.split(" ");

        // assert time equals
        assertEquals(Integer.parseInt(timeItems[0]), Integer.parseInt(cronItems[2]));
        assertEquals(Integer.parseInt(timeItems[1]), Integer.parseInt(cronItems[1]));
        assertEquals(Integer.parseInt(timeItems[2]), Integer.parseInt(cronItems[0]));
        // assert every week
        assertEquals("1", cronItems[3]);
        assertEquals("*", cronItems[4]);
        assertEquals("?", cronItems[5]);
    }
}