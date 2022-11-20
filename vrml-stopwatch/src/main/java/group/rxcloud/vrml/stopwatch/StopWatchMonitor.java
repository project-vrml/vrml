package group.rxcloud.vrml.stopwatch;

import org.springframework.util.StopWatch;

/**
 * {@link StopWatch} with execution monitor.
 */
public class StopWatchMonitor extends StopWatch {

    /**
     * Instantiates a new Stop watch monitor.
     */
    public StopWatchMonitor() {
        super();
    }

    /**
     * Instantiates a new Stop watch monitor.
     *
     * @param id the id
     */
    public StopWatchMonitor(String id) {
        super(id);
    }

    /**
     * Overwrite stop so that add execution monitor.
     */
    @Override
    public void stop() throws IllegalStateException {
        super.stop();

        MonitorInfo monitorInfo = this.getMonitorInfo();
        // do monitor
        Monitors.monitor(monitorInfo);
    }

    private MonitorInfo getMonitorInfo() {
        String id = this.getId();
        String lastTaskName = this.getLastTaskName();
        long totalTimeMillis = this.getTotalTimeMillis();
        long lastTaskTimeMillis = this.getLastTaskTimeMillis();

        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setId(id);
        monitorInfo.setLastTaskName(lastTaskName);
        monitorInfo.setTotalTimeMillis(totalTimeMillis);
        monitorInfo.setTotalTimeMillis(lastTaskTimeMillis);
        monitorInfo.setStopWatch(this);
        return monitorInfo;
    }
}
