package com.kevinten.vrml.compute.config;

public interface ComputeConfiguration<T extends ComputeConfiguration.ComputeConfig> {

    T getComputeConfiguration(String key);

    interface ComputeConfig {

        String getKey();
    }
}
