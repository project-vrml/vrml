package group.rxcloud.vrml.stopwatch.config;

import group.rxcloud.vrml.stopwatch.MonitorInfo;

/**
 * The Monitor configuration.
 */
public interface StopWatchMonitorConfiguration {

    /**
     * Should monitor stopwatch.
     *
     * @param monitorInfo the monitor info
     */
    boolean shouldMonitorStopWatch(MonitorInfo monitorInfo);

    /**
     * Do stopwatch monitor.
     *
     * @param monitorInfo the monitor info
     */
    void doStopWatchMonitor(MonitorInfo monitorInfo);
}
