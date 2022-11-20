package group.rxcloud.vrml.stopwatch.config;

import group.rxcloud.vrml.stopwatch.MonitorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * The Default monitor configuration.
 */
public class DefaultStopWatchLoggerMonitorConfiguration implements StopWatchMonitorConfiguration {

    private final Logger log = LoggerFactory.getLogger(DefaultStopWatchLoggerMonitorConfiguration.class);

    /**
     * Tuned experience values for default out-of-the-box use.
     */
    private static int TUNED_STOPWATCH_EXECUTION_TIMEOUT_MS = 15000;

    private static String MONITOR_LOGGER_LEVEL = "WARN";

    /**
     * Allow users to set the default stopwatch execution monitor time by themselves.
     *
     * @param tunedStopwatchExecutionTimeoutMs the tuned stopwatch execution timeout ms
     */
    public static void setTunedStopwatchExecutionTimeoutMs(int tunedStopwatchExecutionTimeoutMs) {
        TUNED_STOPWATCH_EXECUTION_TIMEOUT_MS = tunedStopwatchExecutionTimeoutMs;
    }

    /**
     * Sets monitor logger level.
     *
     * @param monitorLoggerLevel the logger level
     */
    public static void setMonitorLoggerLevel(String monitorLoggerLevel) {
        MONITOR_LOGGER_LEVEL = monitorLoggerLevel;
    }

    @Override
    public boolean shouldMonitorStopWatch(MonitorInfo monitorInfo) {
        long totalTimeMillis = monitorInfo.getTotalTimeMillis();
        // only monitor execution duration
        return totalTimeMillis > TUNED_STOPWATCH_EXECUTION_TIMEOUT_MS;
    }

    @Override
    public void doStopWatchMonitor(MonitorInfo monitorInfo) {
        final StopWatch stopWatch = monitorInfo.getStopWatch();
        final String record = "[StopWatchMonitor] " +
                "ID = [" + stopWatch.getId() + "], " +
                "TaskName = [" + stopWatch.getLastTaskName() + "], " +
                "RunningTime = [" + stopWatch.getTotalTimeMillis() + "]ms, " +
                "execution too lang.";
        switch (MONITOR_LOGGER_LEVEL) {
            case "info":
            case "INFO":
                log.info(record);
                break;
            case "warn":
            case "WARN":
            case "warning":
            case "WARNING":
                log.warn(record);
                break;
            case "error":
            case "ERROR":
                log.error(record);
                break;
            default:
                log.warn(record);
        }
    }
}
