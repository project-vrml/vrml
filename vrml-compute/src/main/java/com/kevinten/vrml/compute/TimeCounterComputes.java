package com.kevinten.vrml.compute;

import com.kevinten.vrml.compute.config.ComputeConfiguration;
import lombok.Data;

public class TimeCounterComputes extends Computes {

    private TimeCounterComputeConfiguration timeCounterComputeConfiguration;

    @Override
    public void compute(String key, Runnable f) {
        TimeCounterComputeConfig computeConfiguration = timeCounterComputeConfiguration.getComputeConfiguration(key);
    }

    @Override
    public void compute(String key, Runnable f1, Runnable f2) {

    }

    public interface TimeCounterComputeConfiguration extends ComputeConfiguration<TimeCounterComputeConfig> {

        @Override
        TimeCounterComputeConfig getComputeConfiguration(String key);
    }

    @Data
    public static class TimeCounterComputeConfig implements ComputeConfiguration.ComputeConfig {

        private String key;

        private Long expirationTime;

        private Long triggerCount;

        @Override
        public String getKey() {
            return null;
        }
    }
}
