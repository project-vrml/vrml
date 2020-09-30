package io.vrml.time.timezone;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Time zone enum.
 */
public enum TimeZoneEnum {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * TimeZone -12 ~ +12 based on UTC.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Negative : current = UTC - VALUE
     */
    TZ_N12(-12, "-12:00"),
    TZ_N11(-11, "-11:00"),
    TZ_N10(-10, "-10:00"),
    TZ_N09(-9, "-09:00"),
    TZ_N08(-8, "-08:00"),
    TZ_N07(-7, "-07:00"),
    TZ_N06(-6, "-06:00"),
    TZ_N05(-5, "-05:00"),
    TZ_N04(-4, "-04:00"),
    TZ_N03(-3, "-03:00"),
    TZ_N02(-2, "-02:00"),
    TZ_N01(-1, "-01:00"),

    TZ_UTC(+0, "+00:00"),

    /**
     * Positive : current = UTC + VALUE
     */
    TZ_P01(+1, "+01:00"),
    TZ_P02(+2, "+02:00"),
    TZ_P03(+3, "+03:00"),
    TZ_P04(+4, "+04:00"),
    TZ_P05(+5, "+05:00"),
    TZ_P06(+6, "+06:00"),
    TZ_P07(+7, "+07:00"),
    TZ_P08(+8, "+08:00"),
    TZ_P09(+9, "+09:00"),
    TZ_P10(+10, "+10:00"),
    TZ_P11(+11, "+11:00"),
    TZ_P12(+12, "+12:00"),
    ;

    private final int value;
    private final String formula;

    TimeZoneEnum(int value, String formula) {
        this.value = value;
        this.formula = formula;
    }

    /**
     * Gets value.
     *
     * @return the timezone value
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets formula.
     *
     * @return the timezone formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Parse timezone value un safe.
     *
     * @param timezone the timezone value
     * @return the time zone enum
     */
    public static TimeZoneEnum parseValueUnSafe(int timezone) {
        return parseValue(timezone).orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Parse timezone value.
     *
     * @param timezone the timezone value
     * @return the optional time zone enum
     */
    public static Optional<TimeZoneEnum> parseValue(int timezone) {
        return Arrays.stream(TimeZoneEnum.values())
                .filter(timeZoneEnum -> timeZoneEnum.getValue() == timezone)
                .findAny();
    }

    /**
     * Parse timezone formula un safe.
     *
     * @param formula the timezone formula
     * @return the time zone enum
     */
    public static TimeZoneEnum parseFormulaUnSafe(String formula) {
        return parseFormula(formula).orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Parse timezone formula.
     *
     * @param formula the timezone formula
     * @return the optional time zone enum
     */
    public static Optional<TimeZoneEnum> parseFormula(String formula) {
        return Arrays.stream(TimeZoneEnum.values())
                .filter(timeZoneEnum -> timeZoneEnum.getFormula().equals(formula))
                .findAny();
    }

    /**
     * List time zone values.
     *
     * @return the {@link TimeZoneEnum#getValue()} list
     */
    public static List<Integer> listTimeZoneValues() {
        return Arrays.stream(TimeZoneEnum.values())
                .map(TimeZoneEnum::getValue)
                .collect(Collectors.toList());
    }

    /**
     * List time zone formulas.
     *
     * @return the {@link TimeZoneEnum#getFormula()} list
     */
    public static List<String> listTimeZoneFormulas() {
        return Arrays.stream(TimeZoneEnum.values())
                .map(TimeZoneEnum::getFormula)
                .collect(Collectors.toList());
    }

    /**
     * List time zones.
     *
     * @return the {@link TimeZoneEnum} list
     */
    public static List<TimeZoneEnum> listTimeZones() {
        return Arrays.stream(TimeZoneEnum.values())
                .collect(Collectors.toList());
    }
}
