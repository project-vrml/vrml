package com.ten.func.vavr.metric;

import java.lang.annotation.*;

/**
 * {@code @object} metric all fields
 * {@code @field} metric the field
 *
 * @author shihaowang
 * @date 2020/1/1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface MetricFields {

    /**
     * If metric this field
     */
    boolean isMetric() default true;

    /**
     * Metric name
     */
    String key() default "";

    /**
     * If index or store
     */
    boolean isIndex() default true;
}
