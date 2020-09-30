package io.vrml.log.config;

import lombok.Data;

/**
 * The interface Logs configuration.
 */
public interface LogsConfiguration {

    /**
     * Is trace enabled.
     *
     * @param key the key
     * @return {@code true} will log
     */
    boolean isTraceEnabled(String key);

    /**
     * Is debug enabled.
     *
     * @param key the key
     * @return {@code true} will log
     */
    boolean isDebugEnabled(String key);

    /**
     * Is info enabled.
     *
     * @param key the key
     * @return {@code true} will log
     */
    boolean isInfoEnabled(String key);

    /**
     * Is warn enabled.
     *
     * @param key the key
     * @return {@code true} will log
     */
    boolean isWarnEnabled(String key);

    /**
     * Is error enabled.
     *
     * @param key the key
     * @return {@code true} will log
     */
    boolean isErrorEnabled(String key);

    /**
     * The logs config.
     */
    @Data
    final class LogsConfig {

        private String key = "";
        private boolean trace = false;
        private boolean debug = false;
        private boolean info = true;
        private boolean warn = true;
        private boolean error = true;
    }
}

