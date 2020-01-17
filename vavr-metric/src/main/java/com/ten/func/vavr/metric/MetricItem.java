package com.ten.func.vavr.metric;

/**
 * Abstract metric key type. This should be implement by
 * {@link Enum}
 *
 * @author shihaowang
 * @date 2020/1/8
 */
public interface MetricItem {

    /**
     * For {@link Enum} type
     *
     * @return {@link Enum#name()}
     */
    String name();
}
