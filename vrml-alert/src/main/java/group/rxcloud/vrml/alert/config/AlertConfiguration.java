package group.rxcloud.vrml.alert.config;


import group.rxcloud.vrml.alert.actor.AlertMessage;

/**
 * The Alert configuration.
 */
public interface AlertConfiguration {

    /**
     * Is the alert run in async thread pool.
     *
     * @return {@code true} will executed in the async thread pool.
     */
    boolean isAlertAsync();

    /**
     * Is this type of alert enable.
     *
     * @param message the alert message
     * @return {@code true} will do alert process
     */
    boolean isAlertEnable(AlertMessage message);
}
