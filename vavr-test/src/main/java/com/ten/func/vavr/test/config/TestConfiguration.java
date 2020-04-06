package com.ten.func.vavr.test.config;

import java.util.function.Consumer;

/**
 * Unit test configuration.
 */
public interface TestConfiguration {

    /**
     * Only test when {@code true}, you can open this for your localhost test, but close it when pushing to teamcity
     */
    boolean openOneDriveTest = false;

    /**
     * Metric {@code Exception} when test error
     */
    Consumer<Throwable> metricException = System.out::println;
}
