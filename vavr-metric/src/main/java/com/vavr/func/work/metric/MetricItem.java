package com.vavr.func.work.metric;

/**
 * Abstract metric key type. This should be implement by {@link Enum}
 */
public interface MetricItem {

    /**
     * For {@link Enum} type
     *
     * @return {@link Enum#name()}
     */
    String name();
}
