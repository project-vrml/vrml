package com.ten.func.vavr.metric;

import io.vavr.CheckedRunnable;
import io.vavr.Function3;
import lombok.Data;

import java.util.Map;

/**
 * Metric configuration.
 * Please provide your custom {@code configuration} through
 * {@link org.springframework.context.annotation.Configuration} and
 * {@link org.springframework.context.annotation.Bean}
 *
 * @author shihaowang
 * @date 2020/1/1
 */
@Data
public class MetricConfiguration {

    /**
     * Metric logs topic
     */
    private final String topic;

    /**
     * Using {@code <Throwable, IndexMap, StoreMap>} to log exception into metric map
     */
    private final Function3<Throwable, Map<String, String>, Map<String, String>, Void> metricExceptionFunc;

    /**
     * Using {@code <Topic, IndexMap, StoreMap>} to log metric into topic
     */
    private final Function3<String, Map<String, String>, Map<String, String>, Void> logTagsFunc;

    // -- DEFAULT

    /**
     * Metric runnable switch which default true
     * {@link Metric#metric(CheckedRunnable)}
     */
    private boolean metricSwitch = true;
    /**
     * Metric debug runnable switch which default false
     * {@link Metric#debug(CheckedRunnable)}
     */
    private boolean metricDebugSwitch = false;
}


