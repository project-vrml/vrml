package group.rxcloud.vrml.time.cron;

import lombok.Getter;

/**
 * The Cron value enum.
 */
public enum CronValueEnum {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Cron DAY/WEEK/MONTH.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * {@link CronModeEnum#EVERY_DAY}
     */
    EVERY_DAY(0),
    /**
     * {@link CronModeEnum#EVERY_WEEK}
     */
    MONDAY(71),
    TUESDAY(72),
    WEDNESDAY(73),
    THURSDAY(74),
    FRIDAY(75),
    SATURDAY(76),
    SUNDAY(77),
    /**
     * {@link CronModeEnum#EVERY_MONTH}
     */
    MONTH_01(1),
    MONTH_02(2),
    MONTH_03(3),
    MONTH_04(4),
    MONTH_05(5),
    MONTH_06(6),
    MONTH_07(7),
    MONTH_08(8),
    MONTH_09(9),
    MONTH_10(10),
    MONTH_11(11),
    MONTH_12(12),
    MONTH_13(13),
    MONTH_14(14),
    MONTH_15(15),
    MONTH_16(16),
    MONTH_17(17),
    MONTH_18(18),
    MONTH_19(19),
    MONTH_20(20),
    MONTH_21(21),
    MONTH_22(22),
    MONTH_23(23),
    MONTH_24(24),
    MONTH_25(25),
    MONTH_26(26),
    MONTH_27(27),
    MONTH_28(28),
    MONTH_29(29),
    MONTH_30(30),
    MONTH_31(31);

    @Getter
    private final int cronValue;

    CronValueEnum(int cronValue) {
        this.cronValue = cronValue;
    }

    /**
     * Get cron value enum.
     *
     * @param cronValue the cron value
     * @return the cron value enum
     */
    public static CronValueEnum get(int cronValue) {
        for (CronValueEnum cronValueEnum : CronValueEnum.values()) {
            if (cronValueEnum.getCronValue() == cronValue) {
                return cronValueEnum;
            }
        }
        throw new IllegalArgumentException("CronValueEnum=" + cronValue);
    }
}
