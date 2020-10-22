package com.kevinten.vrml.metric;

import com.kevinten.vrml.core.serialization.Serialization;
import com.kevinten.vrml.metric.index.MetricIndex;
import com.kevinten.vrml.metric.store.MetricStore;
import com.kevinten.vrml.metric.value.MetricValue;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.kevinten.vrml.metric.Metrics.*;

/**
 * The Metrics test.
 */
public class MetricsTest {

    /**
     * API demo show
     */
    public void test() {
        metrics((index, store) -> {
            System.out.println(Serialization.GSON.toJson(index));
            System.out.println(Serialization.GSON.toJson(store));
        });
    }

    /**
     * API demo show
     */
    private void metrics(BiConsumer<Map<String, String>, Map<String, String>> howToShow) {
        //  Option 1: add metric manually
        Metrics.metric(() -> {
            // index
            index(MetricIndexs.metric_type, "index");
            // store
            store(MetricStores.context, "store");
            // exception
            exception(new RuntimeException());
            // object
            object(new Object());
        });
        howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
        Metrics.build();

        // Option 2: add metric by automatic placeholder "$"
        Metrics.Log().of(
                // index
                $(MetricIndexs.metric_type, "index"),
                // store
                $(MetricStores.context, "store"),
                // exception
                $(new RuntimeException()),
                // object
                $(new Object())
        );
        howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
        Metrics.build();

        // Also you can use {@code local()} to start a local scope
        String local = Metrics.local();
        Metrics.Log(local).of(
                // Also you can use supplier
                () -> $(MetricIndexs.metric_type, "index"),
                () -> $(MetricStores.context, "store"),
                () -> $(new RuntimeException()),
                () -> $(new Object())
        );
        howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
        Metrics.build(local);
    }
}

/**
 * Metric {@code index} fields
 */
enum MetricIndexs implements MetricIndex {

    /**
     * Metric type indexes
     */
    // Level 1 type
    metric_type,
    ;
}

/**
 * Metric {@code store} fields
 */
enum MetricStores implements MetricStore {

    /**
     * Context
     */
    context,
    ;
}

/**
 * Metric {@code value} fields
 */
enum MetricValues implements MetricValue {

    /**
     * Success metric value
     */
    SUCCESS,
    /**
     * Failure metric value
     */
    FAILURE,
    ;
}
