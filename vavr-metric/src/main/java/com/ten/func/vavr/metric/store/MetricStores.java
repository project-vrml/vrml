package com.ten.func.vavr.metric.store;

/**
 * Metric {@code store} fields
 */
public enum MetricStores implements MetricStore {

    /**
     * Message
     */
    message,

    /**
     * Request
     */
    request,

    /**
     * Response
     */
    response,

    /**
     * Exception
     */
    exception,
    ;
}
