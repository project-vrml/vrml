package group.rxcloud.vrml.stopwatch;

import group.rxcloud.vrml.stopwatch.config.DefaultStopWatchLoggerMonitorConfiguration;
import org.junit.Test;

public class StopWatchMonitorTest {

    @Test
    public void test_id_success() throws InterruptedException {
        DefaultStopWatchLoggerMonitorConfiguration.setTunedStopwatchExecutionTimeoutMs(5000);
        DefaultStopWatchLoggerMonitorConfiguration.setMonitorLoggerLevel("WARN");

        DefaultStopWatchLoggerMonitorConfiguration.setIDStopwatchExecutionTimeoutMs("test-id", 3000);
        DefaultStopWatchLoggerMonitorConfiguration.setIDMonitorLoggerLevel("test-id", "ERROR");

        StopWatchMonitor stopWatchMonitor = new StopWatchMonitor("test-id");

        stopWatchMonitor.start("test-task-name");

        Thread.sleep(4000);

        stopWatchMonitor.stop();
    }

    @Test
    public void test_id_failure() throws InterruptedException {
        DefaultStopWatchLoggerMonitorConfiguration.setTunedStopwatchExecutionTimeoutMs(1000);
        DefaultStopWatchLoggerMonitorConfiguration.setMonitorLoggerLevel("WARN");

        DefaultStopWatchLoggerMonitorConfiguration.setIDStopwatchExecutionTimeoutMs("test-id", 3000);
        DefaultStopWatchLoggerMonitorConfiguration.setIDMonitorLoggerLevel("test-id", "ERROR");

        StopWatchMonitor stopWatchMonitor = new StopWatchMonitor("test-id");

        stopWatchMonitor.start("test-task-name");

        Thread.sleep(2000);

        stopWatchMonitor.stop();
    }

    @Test
    public void test_success() throws InterruptedException {
        DefaultStopWatchLoggerMonitorConfiguration.setTunedStopwatchExecutionTimeoutMs(2000);
        DefaultStopWatchLoggerMonitorConfiguration.setMonitorLoggerLevel("ERROR");

        StopWatchMonitor stopWatchMonitor = new StopWatchMonitor("test-id");

        stopWatchMonitor.start("test-task-name");

        Thread.sleep(3000);

        stopWatchMonitor.stop();
    }

    @Test
    public void test_failure() throws InterruptedException {
        DefaultStopWatchLoggerMonitorConfiguration.setTunedStopwatchExecutionTimeoutMs(2000);
        DefaultStopWatchLoggerMonitorConfiguration.setMonitorLoggerLevel("ERROR");

        StopWatchMonitor stopWatchMonitor = new StopWatchMonitor("test-id");

        stopWatchMonitor.start("test-task-name");

        Thread.sleep(1000);

        stopWatchMonitor.stop();
    }
}