package group.rxcloud.vrml.metric;

import group.rxcloud.vrml.metric.index.MetricIndex;
import group.rxcloud.vrml.metric.store.MetricStore;

import java.lang.annotation.*;

/**
 * {@code @object} will metric all fields,
 * {@code @field} will metric the field.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Metric {

    /**
     * If record this field
     *
     * @return {@code false} will not metric
     */
    boolean isMetric() default true;

    /**
     * Metric's name
     *
     * @return the metric key
     */
    String key() default "";

    /**
     * If {@link Metrics#index(MetricIndex, String)} or {@link Metrics#store(MetricStore, String)}
     *
     * @return isIndex() ? index : store
     */
    boolean isIndex() default true;
}
