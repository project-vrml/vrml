package com.vavr.func.work.metric;

import java.lang.annotation.*;

/**
 * {@code @object} will metric all fields
 * {@code @field} will metric the field
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Metric {

    /**
     * If metric this field
     *
     * @return {@code false} will not metric
     */
    boolean isMetric() default true;

    /**
     * Metric name
     *
     * @return metrics key
     */
    String key() default "";

    /**
     * If index or store
     *
     * @return isIndex() ? index : store
     */
    boolean isIndex() default true;
}
