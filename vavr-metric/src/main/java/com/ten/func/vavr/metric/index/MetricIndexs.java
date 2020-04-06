package com.ten.func.vavr.metric.index;

/**
 * Metric {@code index} fields
 */
public enum MetricIndexs implements MetricIndex {

    /**
     * Metric type indexes
     */
    // Level 1 type
    metric_type,
    // Level 2 type
    specific_metric_type,

    /**
     * Status indexes
     */
    status,
    status_code,
    status_msg,

    /**
     * Process indexes
     */
    start_time,
    end_time,
    duration,

    /**
     * Exception indexes
     */
    exception_type,
    ;
}
