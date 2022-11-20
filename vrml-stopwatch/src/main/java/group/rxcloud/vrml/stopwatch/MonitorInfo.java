package group.rxcloud.vrml.stopwatch;

import org.springframework.util.StopWatch;

public class MonitorInfo {

    private String id;

    private String lastTaskName;

    private long totalTimeMillis;

    private long lastTaskTimeMillis;

    private StopWatch stopWatch;

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastTaskName() {
        return lastTaskName;
    }

    public void setLastTaskName(String lastTaskName) {
        this.lastTaskName = lastTaskName;
    }

    public long getTotalTimeMillis() {
        return totalTimeMillis;
    }

    public void setTotalTimeMillis(long totalTimeMillis) {
        this.totalTimeMillis = totalTimeMillis;
    }

    public long getLastTaskTimeMillis() {
        return lastTaskTimeMillis;
    }

    public void setLastTaskTimeMillis(long lastTaskTimeMillis) {
        this.lastTaskTimeMillis = lastTaskTimeMillis;
    }
}
