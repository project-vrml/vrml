package com.ten.func.vavr.log;

import com.ten.func.vavr.core.beans.SpringContextConfigurator;
import com.ten.func.vavr.log.config.LogsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Logs module.
 */
public final class Logs implements Logger {

    /**
     * API demo.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Logs logs = Factory.getLogs(Logs.class).key(Logs.class.getName());
        logs.info("Hello logs");
    }

    /**
     * Logs factory
     */
    public interface Factory {

        /**
         * Create Logs obj with delegated {@link Logger}
         *
         * @param clazz the clazz
         * @return the logs
         */
        static Logs getLogs(Class<?> clazz) {
            return new Logs(clazz);
        }

        /**
         * Create Logs obj with injected delegate {@link Logger}
         *
         * @param log the log
         * @return the logs
         */
        static Logs getLogs(Logger log) {
            return new Logs(log);
        }
    }

    /**
     * {@link Logger} delegate
     */
    private Logger log;
    /**
     * Logs level depend on key
     */
    private String key;
    /**
     * Logs level configuration by key
     */
    private LogsConfiguration configuration;

    /**
     * Create delegate {@link Logger}
     */
    private Logs(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    /**
     * Inject delegate {@link Logger}
     */
    private Logs(Logger log) {
        this.log = log;
    }

    /**
     * Set key by class name
     *
     * @param clazz the clazz
     * @return the logs
     */
    public Logs key(Class<?> clazz) {
        return this.key(clazz.getSimpleName());
    }

    /**
     * Set custom key
     *
     * @param key the key
     * @return the logs
     */
    public Logs key(String key) {
        Logs logs = this.initKey(key);
        this.initConfig(logs);
        return logs;
    }

    /**
     * Init Logs key value
     *
     * @param key custom key
     * @return Logs with the key
     */
    private Logs initKey(String key) {
        Logs logs;
        if (this.key == null) {
            // init
            this.key = key;
            logs = this;
        } else {
            if (this.key.equalsIgnoreCase(key)) {
                // the same key will use same logs
                logs = this;
            } else {
                // create new logs when diff key
                logs = new Logs(this.log).key(key);
            }
        }
        return logs;
    }

    /**
     * Init Logs configuration from spring context by key
     *
     * @param logs logs obj
     */
    private void initConfig(Logs logs) {
        logs.configuration = SpringContextConfigurator.getBean(LogsConfiguration.class);
    }

    // -- Delegate Logger

    @Override
    public String getName() {
        return log.getName();
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled() && configuration.isTraceEnabled(key);
    }

    @Override
    public void trace(String msg) {
        log.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        log.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        log.trace(msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker) && configuration.isTraceEnabled(key);
    }

    @Override
    public void trace(Marker marker, String msg) {
        log.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        log.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        log.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        log.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        log.trace(marker, msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled() && configuration.isDebugEnabled(key);
    }

    @Override
    public void debug(String msg) {
        log.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        log.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log.debug(msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker) && configuration.isDebugEnabled(key);
    }

    @Override
    public void debug(Marker marker, String msg) {
        log.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        log.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        log.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        log.debug(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        log.debug(marker, msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled() && configuration.isInfoEnabled(key);
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        log.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        log.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log.info(msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker) && configuration.isInfoEnabled(key);
    }

    @Override
    public void info(Marker marker, String msg) {
        log.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        log.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        log.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        log.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        log.info(marker, msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled() && configuration.isWarnEnabled(key);
    }

    @Override
    public void warn(String msg) {
        log.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        log.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log.warn(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log.warn(msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker) && configuration.isWarnEnabled(key);
    }

    @Override
    public void warn(Marker marker, String msg) {
        log.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        log.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        log.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        log.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        log.warn(marker, msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled() && configuration.isErrorEnabled(key);
    }

    @Override
    public void error(String msg) {
        log.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        log.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        log.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log.error(msg, t);
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker) && configuration.isErrorEnabled(key);
    }

    @Override
    public void error(Marker marker, String msg) {
        log.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        log.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        log.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        log.error(marker, msg, t);
    }
}
