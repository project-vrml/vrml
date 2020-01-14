package com.ten.func.vavr.work.test;

import java.util.function.Consumer;

/**
 * Unit test configuration
 *
 * @author shihaowang
 * @date 2019/12/25
 */
public interface TestConfiguration {

    /**
     * Only test when true for your localhost test
     */
    boolean openOneDriveTest = false;

    Runnable metric = () -> {
    };

    Consumer<String> metricTopic = topic -> {
    };

    /**
     * Metric {@code Exception} when test error
     */
    Consumer<Throwable> metricException = throwable -> {
    };
}
