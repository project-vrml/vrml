package group.rxcloud.vrml.stopwatch.config;

import group.rxcloud.vrml.stopwatch.MonitorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class DefaultMonitorConfiguration implements MonitorConfiguration {

    private final Logger log = LoggerFactory.getLogger(DefaultMonitorConfiguration.class);

    @Override
    public boolean isMonitorEnabled(MonitorInfo monitorInfo) {
        return false;
    }

    @Override
    public void doMonitor(MonitorInfo monitorInfo) {
        StopWatch stopWatch = monitorInfo.getStopWatch();
        log.error(stopWatch.shortSummary());
    }
}
