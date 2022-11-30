package group.rxcloud.vrml.time.cron;


/**
 * The Cron mode enum.
 */
public enum CronModeEnum {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Cron DAY/WEEK/MONTH.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Cron execution mode
     */
    EVERY_DAY("EVERY_DAY"),
    EVERY_WEEK("EVERY_WEEK"),
    EVERY_MONTH("EVERY_MONTH");

    private final String cronMode;

    public String getCronMode() {
        return cronMode;
    }

    CronModeEnum(String cronMode) {
        this.cronMode = cronMode;
    }

    /**
     * Get cron mode enum.
     *
     * @param cronMode the cron mode
     * @return the cron mode enum
     */
    public static CronModeEnum get(String cronMode) {
        for (CronModeEnum cronModeEnum : CronModeEnum.values()) {
            if (cronModeEnum.getCronMode().equalsIgnoreCase(cronMode)) {
                return cronModeEnum;
            }
        }
        throw new IllegalArgumentException("CronModeEnum=" + cronMode);
    }
}
