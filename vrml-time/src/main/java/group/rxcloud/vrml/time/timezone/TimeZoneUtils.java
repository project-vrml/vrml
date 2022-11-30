package group.rxcloud.vrml.time.timezone;

import group.rxcloud.vrml.time.calculation.ThreadLocalTimeUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * The Time zone utils.
 */
public abstract class TimeZoneUtils {

    private static final String JDK_TIMEZONE_GMT_FORMAT = "GMT";

    /**
     * Parse timezone timestamp.
     *
     * @param sourceTimeZoneEnum the source time zone enum
     * @param targetTimeZoneEnum the target time zone enum
     * @param sourceTimestamp    the source timestamp
     * @return the target timestamp
     * @throws ParseException the parse exception
     *                        https ://blog.csdn.net/wanglq0086/article/details/61920364
     */
    public static Timestamp parseTimezoneTimestamp(TimeZoneEnum sourceTimeZoneEnum,
                                                   TimeZoneEnum targetTimeZoneEnum,
                                                   Timestamp sourceTimestamp) throws ParseException {
        if (targetTimeZoneEnum == sourceTimeZoneEnum) {
            return sourceTimestamp;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(ThreadLocalTimeUtils.DATE_TIME_FORMAT);

        // parse timestamp to source timezone date
        TimeZone sourceTimeZone = TimeZone.getTimeZone(JDK_TIMEZONE_GMT_FORMAT + sourceTimeZoneEnum.getFormula());
        dateFormat.setTimeZone(sourceTimeZone);

        String formatDateTime = ThreadLocalTimeUtils.formatDateTime(sourceTimestamp);
        Date sourceDate = dateFormat.parse(formatDateTime);

        // parse source timezone date to target timezone
        TimeZone targetTimeZone = TimeZone.getTimeZone(JDK_TIMEZONE_GMT_FORMAT + targetTimeZoneEnum.getFormula());
        dateFormat.setTimeZone(targetTimeZone);

        String zoneDateTime = dateFormat.format(sourceDate);

        return new Timestamp(ThreadLocalTimeUtils.parseDateTime(zoneDateTime).getTime());
    }
}
