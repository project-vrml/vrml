package group.rxcloud.vrml.stopwatch;

import group.rxcloud.vrml.spi.SPI;
import group.rxcloud.vrml.stopwatch.config.DefaultStopWatchLoggerMonitorConfiguration;
import group.rxcloud.vrml.stopwatch.config.StopWatchMonitorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StopWatch Monitors.
 */
final class Monitors {

    private static final Logger LOG = LoggerFactory.getLogger(Monitors.class);

    private static StopWatchMonitorConfiguration monitorConfiguration;

    static {
        try {
            monitorConfiguration = SPI.loadSpiImpl(
                    StopWatchMonitorConfiguration.class,
                    // use default configuration when non spi impl
                    DefaultStopWatchLoggerMonitorConfiguration::new);
        } catch (Exception e) {
            // use default configuration when non spi impl
            monitorConfiguration = new DefaultStopWatchLoggerMonitorConfiguration();
            LOG.warn("[Vrml][StopWatchMonitors.init] load monitor configuration spi failure! use default.", e);
        }
    }

    /**
     * Monitor StopWatch execution.
     *
     * @param monitorInfo the monitor info
     */
    public static void monitor(MonitorInfo monitorInfo) {
        if (monitorConfiguration.shouldMonitorStopWatch(monitorInfo)) {
            monitorConfiguration.doStopWatchMonitor(monitorInfo);
        }
    }
}
