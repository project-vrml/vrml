package group.rxcloud.vrml.api.intercept.config;

/**
 * The Api Log configuration.
 */
public interface ApiLogConfiguration {

    /**
     * Is open request log.
     *
     * @param logsKey the logs key
     * @return {@code true} will log
     */
    boolean isOpenRequestLog(String logsKey);

    /**
     * Is open response log.
     *
     * @param logsKey the logs key
     * @return {@code true} will log
     */
    boolean isOpenResponseLog(String logsKey);

    /**
     * Is open error log.
     *
     * @param logsKey the logs key
     * @return {@code true} will log
     */
    boolean isOpenErrorLog(String logsKey);
}
