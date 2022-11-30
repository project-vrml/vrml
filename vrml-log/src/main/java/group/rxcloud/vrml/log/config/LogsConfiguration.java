package group.rxcloud.vrml.log.config;


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
    final class LogsConfig {

        private String key = "";
        private boolean trace = false;
        private boolean debug = false;
        private boolean info = true;
        private boolean warn = true;
        private boolean error = true;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isTrace() {
            return trace;
        }

        public void setTrace(boolean trace) {
            this.trace = trace;
        }

        public boolean isDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }

        public boolean isInfo() {
            return info;
        }

        public void setInfo(boolean info) {
            this.info = info;
        }

        public boolean isWarn() {
            return warn;
        }

        public void setWarn(boolean warn) {
            this.warn = warn;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }
    }
}

