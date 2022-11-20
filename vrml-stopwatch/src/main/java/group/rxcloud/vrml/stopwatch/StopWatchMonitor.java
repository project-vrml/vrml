package group.rxcloud.vrml.stopwatch;

import org.springframework.util.StopWatch;

public class StopWatchMonitor extends StopWatch {

    public StopWatchMonitor() {
        super();
    }

    public StopWatchMonitor(String id) {
        super(id);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();

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

        Monitors.monitor(monitorInfo);
    }
}
