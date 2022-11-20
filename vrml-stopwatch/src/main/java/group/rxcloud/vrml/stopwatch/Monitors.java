package group.rxcloud.vrml.stopwatch;

import group.rxcloud.vrml.spi.SPI;
import group.rxcloud.vrml.stopwatch.config.DefaultMonitorConfiguration;
import group.rxcloud.vrml.stopwatch.config.MonitorConfiguration;

import java.util.Optional;

@Slf4j
public class Monitors {

    private static MonitorConfiguration monitorConfiguration;

    static {
        try {
            monitorConfiguration = SPI.loadSpiImpl(
                    MonitorConfiguration.class,
                    DefaultMonitorConfiguration::new);
        } catch (Exception e) {
            monitorConfiguration = new DefaultMonitorConfiguration();
        }
    }

    public static void monitor(MonitorInfo monitorInfo) {
        monitorConfiguration.doMonitor(monitorInfo);
    }
}
