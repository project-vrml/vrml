package group.rxcloud.vrml.stopwatch.config;

import group.rxcloud.vrml.metric.Metrics;
import group.rxcloud.vrml.stopwatch.MonitorInfo;
import group.rxcloud.vrml.stopwatch.metric.StopWatchMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Values for specific ID use.
     */
    private static final Map<String, Integer> ID_TIMEOUT_MAP = new HashMap<>();
    private static final Map<String, String> ID_LEVEL_MAP = new HashMap<>();

    /**
     * Allow users to set the default stopwatch execution monitor time by themselves.
     *
     * @param id                            the id
     * @param idStopwatchExecutionTimeoutMs the id stopwatch execution timeout ms
     */
    public static void setIDStopwatchExecutionTimeoutMs(String id, int idStopwatchExecutionTimeoutMs) {
        ID_TIMEOUT_MAP.put(id, idStopwatchExecutionTimeoutMs);
    }

    /**
     * Sets monitor logger level.
     *
     * @param id                   the id
     * @param idMonitorLoggerLevel the id monitor logger level
     */
    public static void setIDMonitorLoggerLevel(String id, String idMonitorLoggerLevel) {
        ID_LEVEL_MAP.put(id, idMonitorLoggerLevel);
    }

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
        // only monitor execution duration
        long totalTimeMillis = monitorInfo.getTotalTimeMillis();

        int timeout = this.getTimeout(monitorInfo);
        return totalTimeMillis > timeout;
    }

    protected int getTimeout(MonitorInfo monitorInfo) {
        String id = monitorInfo.getId();
        if (ID_TIMEOUT_MAP.containsKey(id)) {
            return ID_TIMEOUT_MAP.get(id);
        } else {
            return TUNED_STOPWATCH_EXECUTION_TIMEOUT_MS;
        }
    }

    @Override
    public void doStopWatchMonitor(MonitorInfo monitorInfo) {
        String id = monitorInfo.getId();
        try {
            String record = this.getLogMessage(monitorInfo);

            Map<String, String> indexes = Metrics.showIndexs(id);
            if (!indexes.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("[[");
                String tags = indexes.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining(","));
                builder.append(tags);
                builder.append("]]");

                builder.append(record);

                record = builder.toString();
            }

            String logLevel = this.getLogLevel(monitorInfo);
            switch (logLevel) {
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
        } finally {
            Metrics.remove(id);
        }
    }

    protected String getLogMessage(MonitorInfo monitorInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("[StopWatchMonitor] ");

        String id = monitorInfo.getId();
        if (id != null) {
            builder.append("ID = [").append(id).append("], ");
            {
                Metrics.index(id, StopWatchMetric.ID, id);
            }
        }

        String taskName = monitorInfo.getLastTaskName();
        if (taskName != null) {
            builder.append("TaskName = [").append(taskName).append("], ");
            {
                Metrics.index(id, StopWatchMetric.TaskName, taskName);
            }
        }

        long totalTimeMillis = monitorInfo.getTotalTimeMillis();
        if (totalTimeMillis > 0) {
            builder.append("RunningTime = [").append(totalTimeMillis).append("]ms, ");
            {
                Metrics.index(id, StopWatchMetric.RunningTime, String.valueOf(totalTimeMillis));
            }
        }

        if (monitorInfo.getThrowable() != null) {
            builder.append("ErrorMessage = [").append(monitorInfo.getThrowable().getMessage()).append("], ");
        }

        if (monitorInfo.getInfo() != null) {
            builder.append("ExInfo = [").append(monitorInfo.getInfo()).append("], ");
        }

        builder.append("execution too lang.");
        return builder.toString();
    }

    protected String getLogLevel(MonitorInfo monitorInfo) {
        String id = monitorInfo.getId();
        if (ID_LEVEL_MAP.containsKey(id)) {
            return ID_LEVEL_MAP.get(id);
        } else {
            return MONITOR_LOGGER_LEVEL;
        }
    }
}
