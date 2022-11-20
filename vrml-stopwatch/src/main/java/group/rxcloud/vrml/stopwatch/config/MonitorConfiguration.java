package group.rxcloud.vrml.stopwatch.config;

import group.rxcloud.vrml.stopwatch.MonitorInfo;

public interface MonitorConfiguration {

    boolean isMonitorEnabled(MonitorInfo monitorInfo);

    void doMonitor(MonitorInfo monitorInfo);
}
