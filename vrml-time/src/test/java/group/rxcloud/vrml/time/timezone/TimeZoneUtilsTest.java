package group.rxcloud.vrml.time.timezone;

import group.rxcloud.vrml.time.calculation.ThreadLocalTimeUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertNotEquals;

/**
 * The Time zone utils test.
 */
public class TimeZoneUtilsTest extends TestCase {

    /**
     * Parse timezone timestamp.
     */
    @Test
    public void testParseTimezoneTimestamp() throws ParseException {
        Timestamp current = ThreadLocalTimeUtils.currentTimestamp();
        String currentFormat = ThreadLocalTimeUtils.formatDateTime(current);
        Date currentDate = ThreadLocalTimeUtils.parseDateTime(currentFormat);
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

        // for timezone list as source
        for (TimeZoneEnum sourceTimezone : TimeZoneEnum.listTimeZones()) {
            // for timezone list as target
            for (TimeZoneEnum targetTimezone : TimeZoneEnum.listTimeZones()) {

                // parse source timezone time to target timezone time
                Timestamp targetTimestamp = TimeZoneUtils.parseTimezoneTimestamp(sourceTimezone, targetTimezone, currentTimestamp);
                if (sourceTimezone != targetTimezone) {
                    assertNotEquals(targetTimestamp.getTime(), currentTimestamp.getTime());
                } else {
                    assertEquals(targetTimestamp.getTime(), currentTimestamp.getTime());
                }

                // parse target timezone time to source timezone time
                Timestamp sourceTimestamp = TimeZoneUtils.parseTimezoneTimestamp(targetTimezone, sourceTimezone, targetTimestamp);
                assertEquals(currentTimestamp.getTime(), sourceTimestamp.getTime());
            }
        }
    }
}