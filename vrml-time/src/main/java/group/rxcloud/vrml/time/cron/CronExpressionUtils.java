package group.rxcloud.vrml.time.cron;

import group.rxcloud.vrml.time.calculation.ThreadLocalTimeUtils;
import group.rxcloud.vrml.time.timezone.TimeZoneEnum;
import group.rxcloud.vrml.time.timezone.TimeZoneUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.util.Date;

import static group.rxcloud.vrml.time.cron.CronValueEnum.*;

/**
 * The Cron expression utils.
 */
public abstract class CronExpressionUtils {

    // -- Params builder

    /**
     * The Cron parse builder.
     */
    @Data
    public static class CronParseBuilder {
        /**
         * Cron mode
         */
        private CronModeEnum cronModeEnum;
        /**
         * Week value if needed
         */
        private Integer dayOfWeek;
        /**
         * Month value if needed
         */
        private Integer dayOfMonth;
    }

    /**
     * The Simple cron parse builder.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SimpleCronParseBuilder extends CronParseBuilder {
        /**
         * Execute time
         */
        private Timestamp executeTime;
    }

    /**
     * The Time zone cron parse builder.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TimeZoneCronParseBuilder extends CronParseBuilder {
        /**
         * Source execute time
         */
        private Timestamp sourceExecuteTime;
        /**
         * Source timezone
         */
        private TimeZoneEnum sourceTimeZone;
        /**
         * Target timezone
         */
        private TimeZoneEnum targetTimeZone;
    }

    // -- Cron parser

    /**
     * Parse cron to target timezone.
     *
     * @param builder the builder
     * @return the target timezone cron
     */
    @SneakyThrows
    public static String parseCronToTargetTimeZone(TimeZoneCronParseBuilder builder) {
        Timestamp targetTimestamp = TimeZoneUtils.parseTimezoneTimestamp(builder.getSourceTimeZone(), builder.getTargetTimeZone(), builder.getSourceExecuteTime());

        CronValueEnum cronValueEnum = CronParseHelper.parseTimeZoneCronValueBy(builder, targetTimestamp);

        String targetExecuteTime = ThreadLocalTimeUtils.formatTime(targetTimestamp);

        return convertToCron(cronValueEnum, targetExecuteTime);
    }

    /**
     * Parse cron.
     *
     * @param builder the params builder
     * @return the cron
     */
    public static String parseCron(SimpleCronParseBuilder builder) {
        CronValueEnum cronValueEnum = CronParseHelper.parseCronValue(builder.getCronModeEnum(), builder.getDayOfWeek(), builder.getDayOfMonth());

        String targetExecuteTime = ThreadLocalTimeUtils.formatTime(builder.getExecuteTime());

        return convertToCron(cronValueEnum, targetExecuteTime);
    }

    // -- Cron expression

    private static final String TIME_SPLIT = ":";
    private static final String SPLIT = " ";
    private static final String EVERY_DAY_SUFFIX = "* * ?";
    private static final String EVERY_WEEK_SUFFIX = "? * ";
    private static final String EVERY_MONTH_SUFFIX = " * ?";

    /**
     * Resolve into Cron expressions based on execution time and execution mode.
     */
    private static String convertToCron(CronValueEnum cronValueEnum, String executeTime) {
        StringBuilder cron = new StringBuilder();

        String[] times = executeTime.trim().split(TIME_SPLIT);

        for (int i = times.length - 1; i >= 0; i--) {
            cron.append(Integer.parseInt(times[i]));
            cron.append(SPLIT);
        }

        int mode = cronValueEnum.getCronValue();

        // EVERY DAY
        if (mode == 0) {
            cron.append(EVERY_DAY_SUFFIX);
        }
        // EVERY WEEK
        else if (mode >= 71 && mode <= 77) {
            // Cron X -> X+1 day of week
            int week = mode - 70 + 1;
            if (week == 8) {
                // Cron 1 -> sunday
                week = 1;
            }
            cron.append(EVERY_WEEK_SUFFIX);
            cron.append(week);
        }
        // EVERY MONTH
        else if (mode >= 1 && mode <= 31) {
            cron.append(mode);
            cron.append(EVERY_MONTH_SUFFIX);
        }

        return cron.toString();
    }

    /**
     * Cron parse helper
     */
    private static class CronParseHelper {

        private static final long TARGET_INCR_ONE_DAY = 1;
        private static final long TARGET_DECR_ONE_DAY = -1;

        /**
         * Parse time zone cron value.
         *
         * @param builder         the builder
         * @param targetTimestamp the target timestamp
         * @return the cron value enum
         */
        public static CronValueEnum parseTimeZoneCronValueBy(TimeZoneCronParseBuilder builder, Timestamp targetTimestamp) {
            Date sourceDate = new Date(builder.getSourceExecuteTime().getTime());
            Date targetDate = new Date(targetTimestamp.getTime());
            long timezoneCronDateChange = ThreadLocalTimeUtils.differentDays(targetDate, sourceDate);

            // parse timezone execution date change
            if (timezoneCronDateChange == TARGET_INCR_ONE_DAY) {
                // target = source + TARGET_INCR_ONE_DAY
                return parsePeriodDateIncr(builder.getCronModeEnum(), builder.getDayOfWeek(), builder.getDayOfMonth());
            } else if (timezoneCronDateChange == TARGET_DECR_ONE_DAY) {
                // target = source + TARGET_DECR_ONE_DAY
                return parsePeriodDateDecr(builder.getCronModeEnum(), builder.getDayOfWeek(), builder.getDayOfMonth());
            } else {
                // target = source
                return parseCronValue(builder.getCronModeEnum(), builder.getDayOfWeek(), builder.getDayOfMonth());
            }
        }

        /**
         * Minus one day: missions on 1st May be executed on 31st (nonexistent)
         *
         * @param cronModeEnum the cron mode enum
         * @param dayOfWeek    the day of week
         * @param dayOfMonth   the day of month
         * @return the cron value enum
         */
        public static CronValueEnum parsePeriodDateDecr(CronModeEnum cronModeEnum, Integer dayOfWeek, Integer dayOfMonth) {
            switch (cronModeEnum) {
                case EVERY_DAY:
                    return EVERY_DAY;
                case EVERY_WEEK:
                    int originWeek = 70 + dayOfWeek;
                    if (originWeek == MONDAY.getCronValue()) {
                        // MONDAY -> SUNDAY
                        return SUNDAY;
                    } else {
                        return get(70 + dayOfWeek - 1);
                    }
                case EVERY_MONTH:
                    if (dayOfMonth == MONTH_01.getCronValue()) {
                        // MONTH_01 -> MONTH_31
                        return MONTH_31;
                    } else {
                        return get(dayOfMonth - 1);
                    }
                default:
                    throw new IllegalArgumentException(cronModeEnum.name());
            }
        }

        /**
         * Plus one day: missions on the 30th May be executed on the 31st (nonexistent)
         *
         * @param cronModeEnum the cron mode enum
         * @param dayOfWeek    the day of week
         * @param dayOfMonth   the day of month
         * @return the cron value enum
         */
        public static CronValueEnum parsePeriodDateIncr(CronModeEnum cronModeEnum, Integer dayOfWeek, Integer dayOfMonth) {
            switch (cronModeEnum) {
                case EVERY_DAY:
                    return EVERY_DAY;
                case EVERY_WEEK:
                    int originWeek = 70 + dayOfWeek;
                    if (originWeek == SUNDAY.getCronValue()) {
                        // SUNDAY -> MONDAY
                        return MONDAY;
                    } else {
                        return get(70 + dayOfWeek + 1);
                    }
                case EVERY_MONTH:
                    if (dayOfMonth == MONTH_31.getCronValue()) {
                        // MONTH_31 -> MONTH_01
                        return MONTH_01;
                    } else {
                        return get(dayOfMonth + 1);
                    }
                default:
                    throw new IllegalArgumentException(cronModeEnum.name());
            }
        }

        /**
         * Parse cron value.
         *
         * @param cronModeEnum the cron mode enum
         * @param dayOfWeek    the day of week
         * @param dayOfMonth   the day of month
         * @return the cron value enum
         */
        public static CronValueEnum parseCronValue(CronModeEnum cronModeEnum, Integer dayOfWeek, Integer dayOfMonth) {
            switch (cronModeEnum) {
                case EVERY_DAY:
                    return EVERY_DAY;
                case EVERY_WEEK:
                    return get(70 + dayOfWeek);
                case EVERY_MONTH:
                    return get(dayOfMonth);
                default:
                    throw new IllegalArgumentException(cronModeEnum.name());
            }
        }
    }
}
