package group.rxcloud.vrml.stopwatch;

import org.springframework.util.StopWatch;

/**
 * {@link StopWatch} with execution monitor.
 */
public class StopWatchMonitor extends StopWatch {

    private Throwable throwable;
    private String info;

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

    public void withError(Throwable throwable) {
        this.throwable = throwable;
    }

    public void withInfo(String info) {
        this.info = info;
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
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setId(this.getId());
        monitorInfo.setLastTaskName(this.getLastTaskName());
        monitorInfo.setTotalTimeMillis(this.getTotalTimeMillis());
        monitorInfo.setTotalTimeMillis(this.getLastTaskTimeMillis());
        monitorInfo.setThrowable(this.throwable);
        monitorInfo.setInfo(this.info);
        monitorInfo.setStopWatch(this);
        return monitorInfo;
    }
}
