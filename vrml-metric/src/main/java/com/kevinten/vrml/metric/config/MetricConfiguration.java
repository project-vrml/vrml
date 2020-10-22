package com.kevinten.vrml.metric.config;

import com.kevinten.vrml.metric.Metrics;
import io.vavr.CheckedRunnable;
import io.vavr.Function3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Metric configuration.
 * Please provide your custom {@code configuration} through
 * {@link org.springframework.context.annotation.Configuration} and
 * {@link org.springframework.context.annotation.Bean}
 */
public interface MetricConfiguration {

    /**
     * Metric logs topic.
     *
     * @return the topic.
     */
    String topic();

    /**
     * Using {@code <Throwable, IndexMap, StoreMap>} to log exception into metric map.
     *
     * @return the exception metric function.
     */
    Function3<Throwable, Map<String, String>, Map<String, String>, Void> metricException();

    /**
     * Using {@code <Topic, IndexMap, StoreMap>} to log metric into topic.
     *
     * @return the finally metrics logging function.
     */
    Function3<String, Map<String, String>, Map<String, String>, Void> metricFinally();

    /**
     * Metric runnable switch which default true
     * {@link Metrics#metric(CheckedRunnable)}
     *
     * @return {@code true} will run {@link Metrics#metric(CheckedRunnable)}
     */
    boolean metricSwitch();

    /**
     * Metric debug runnable switch which default false
     * {@link Metrics#debug(CheckedRunnable)}
     *
     * @return {@code true} will run {@link Metrics#debug(CheckedRunnable)}
     */
    boolean debugSwitch();

    /**
     * Build exception stack readable
     *
     * @param cause throwable
     * @return A readable stack
     */
    default String buildExceptionStack(Throwable cause) {
        if (cause != null) {
            StringWriter stringWriter = new StringWriter(MetricModule.EXCEPTION_STACK_SIZE);
            cause.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        } else {
            return MetricModule.EXCEPTION_STACK_UNKNOWN;
        }
    }
}

// -- METRIC MODULE

/**
 * The Metric package module.
 */
interface MetricModule {

    /**
     * The constant EXCEPTION_STACK_SIZE.
     */
    int EXCEPTION_STACK_SIZE = 2048;

    /**
     * The constant EXCEPTION_STACK_UNKNOWN.
     */
    String EXCEPTION_STACK_UNKNOWN = "Empty exception!";
}