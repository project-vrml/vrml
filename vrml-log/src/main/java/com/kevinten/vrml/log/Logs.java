package com.kevinten.vrml.log;

import com.kevinten.vrml.core.beans.SpringContextConfigurator;
import com.kevinten.vrml.log.config.LogsConfiguration;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Logs module.
 */
public final class Logs implements Logger {

    private static final String KEY_TAG = "key";

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
    private Lazy<LogsConfiguration> configuration;

    /**
     * Create delegate {@link Logger}
     */
    private Logs(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
        this.initConfig(this);
    }

    /**
     * Inject delegate {@link Logger}
     */
    private Logs(Logger log) {
        this.log = log;
        this.initConfig(this);
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
        return this.initKey(key);
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
     *             lazy load spring context, avoid spring context delayed injection
     */
    private void initConfig(Logs logs) {
        logs.configuration = Lazy.of(() -> SpringContextConfigurator.getBean(LogsConfiguration.class));
    }

    // -- Tag

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Use tag helper to tag default key.
     *
     * @return the logs tag helper
     */
    public LogsTagHelper tagKey() {
        return new LogsTagHelper(this).tag(KEY_TAG, getKey());
    }

    /**
     * Use tag helper to tag key.
     *
     * @param logsKey the log tag key
     * @return the logs tag helper
     */
    public LogsTagHelper tagKey(String logsKey) {
        return new LogsTagHelper(this).tag(KEY_TAG, logsKey);
    }

    /**
     * Use tag helper to tag something.
     *
     * @param key   the tag key
     * @param value the tag value
     * @return the logs tag helper
     */
    public LogsTagHelper tag(String key, String value) {
        return new LogsTagHelper(this).tag(key, value);
    }

    /**
     * The Logs tag helper.
     * <p>
     * use {@code MDC} in {@code ThreadLocal}
     */
    public static final class LogsTagHelper {

        private final Logs logs;
        private final Map<String, String> tags;
        private boolean clear = false;

        private LogsTagHelper(Logs logs) {
            this.logs = logs;
            this.tags = new HashMap<>(2);
        }

        /**
         * Tag logs tag helper.
         *
         * @param key   the tag key
         * @param value the tag value
         * @return the logs tag helper
         */
        public LogsTagHelper tag(String key, String value) {
            MDC.put(key, value);
            tags.put(key, value);
            return this;
        }

        /**
         * Do log process, andThen remove tag keys from {@code MDC}.
         *
         * @param logsConsumer the logs consumer
         */
        public void build(Consumer<Logs> logsConsumer) {
            Try.run(() -> logsConsumer.accept(logs))
                    .andFinally(this::removeTempTags);
        }

        private void removeTempTags() {
            tags.forEach((tagKey, value) -> MDC.remove(tagKey));
            clear = true;
        }

        /**
         * When you forget to remove MDC, take error to notify
         */
        @Override
        protected void finalize() throws Throwable {
            if (!clear && !CollectionUtils.isEmpty(tags)) {
                String tagsInfo = tags.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));
                logs.error("[LogsTagHelper.finalize] you haven't remove tags[{}] from MDC.", tagsInfo);
            }
        }
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
        return log.isTraceEnabled() && configuration.get().isTraceEnabled(key);
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) {
            log.trace(msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            log.trace(format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            log.trace(format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            log.trace(format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            log.trace(msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker) && configuration.get().isTraceEnabled(key);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (isTraceEnabled(marker)) {
            log.trace(marker, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (isTraceEnabled(marker)) {
            log.trace(marker, format, arg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isTraceEnabled(marker)) {
            log.trace(marker, format, arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (isTraceEnabled(marker)) {
            log.trace(marker, format, argArray);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (isTraceEnabled(marker)) {
            log.trace(marker, msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled() && configuration.get().isDebugEnabled(key);
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) {
            log.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            log.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            log.debug(format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            log.debug(format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            log.debug(msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker) && configuration.get().isDebugEnabled(key);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (isDebugEnabled(marker)) {
            log.debug(marker, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (isDebugEnabled(marker)) {
            log.debug(marker, format, arg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isDebugEnabled(marker)) {
            log.debug(marker, format, arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (isDebugEnabled(marker)) {
            log.debug(marker, format, arguments);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (isDebugEnabled(marker)) {
            log.debug(marker, msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled() && configuration.get().isInfoEnabled(key);
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) {
            log.info(msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            log.info(format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            log.info(format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            log.info(format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            log.info(msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker) && configuration.get().isInfoEnabled(key);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (isInfoEnabled(marker)) {
            log.info(marker, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (isInfoEnabled(marker)) {
            log.info(marker, format, arg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isInfoEnabled(marker)) {
            log.info(marker, format, arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (isInfoEnabled(marker)) {
            log.info(marker, format, arguments);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (isInfoEnabled(marker)) {
            log.info(marker, msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled() && configuration.get().isWarnEnabled(key);
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) {
            log.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            log.warn(format, arg);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            log.warn(format, arguments);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            log.warn(format, arg1, arg2);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            log.warn(msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker) && configuration.get().isWarnEnabled(key);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (isWarnEnabled(marker)) {
            log.warn(marker, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (isWarnEnabled(marker)) {
            log.warn(marker, format, arg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isWarnEnabled(marker)) {
            log.warn(marker, format, arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (isWarnEnabled(marker)) {
            log.warn(marker, format, arguments);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (isWarnEnabled(marker)) {
            log.warn(marker, msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled() && configuration.get().isErrorEnabled(key);
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) {
            log.error(msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            log.error(format, arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            log.error(format, arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            log.error(format, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            log.error(msg, t);
        }
    }

    /**
     * @return both {@link #log} and {@link #configuration} config
     */
    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker) && configuration.get().isErrorEnabled(key);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (isErrorEnabled(marker)) {
            log.error(marker, msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (isErrorEnabled(marker)) {
            log.error(marker, format, arg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isErrorEnabled(marker)) {
            log.error(marker, format, arg1, arg2);
        }
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (isErrorEnabled(marker)) {
            log.error(marker, format, arguments);
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (isErrorEnabled(marker)) {
            log.error(marker, msg, t);
        }
    }
}
