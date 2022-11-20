package group.rxcloud.vrml.stopwatch;

import group.rxcloud.vrml.stopwatch.config.DefaultStopWatchLoggerMonitorConfiguration;
import lombok.SneakyThrows;
import org.junit.Test;

public class StopWatchMonitorTest {

    @SneakyThrows
    @Test
    public void test_success() {
        DefaultStopWatchLoggerMonitorConfiguration.setTunedStopwatchExecutionTimeoutMs(2000);
        DefaultStopWatchLoggerMonitorConfiguration.setMonitorLoggerLevel("ERROR");

        StopWatchMonitor stopWatchMonitor = new StopWatchMonitor("test-id");

        stopWatchMonitor.start("test-task-name");

        Thread.sleep(3000);

        stopWatchMonitor.stop();
    }

    @SneakyThrows
    @Test
    public void test_failure() {
        DefaultStopWatchLoggerMonitorConfiguration.setTunedStopwatchExecutionTimeoutMs(2000);
        DefaultStopWatchLoggerMonitorConfiguration.setMonitorLoggerLevel("ERROR");

        StopWatchMonitor stopWatchMonitor = new StopWatchMonitor("test-id");

        stopWatchMonitor.start("test-task-name");

        Thread.sleep(1000);

        stopWatchMonitor.stop();
    }
}