package com.vavr.func.work.metric;

import com.vavr.func.work.metric.index.MetricIndexs;
import com.vavr.func.work.metric.store.MetricStores;
import com.vavr.func.work.test.Tests;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static com.vavr.func.work.metric.Metrics.$;


/**
 * {@link Metrics} API test.
 */
public class MetricsTest implements Tests {

    @Test
    public void metric1() {
        //  Option 1: add metric manually
        Metrics.metric(() -> {
            // index
            Metrics.index(MetricIndexs.metric_type, "index");
            // store
            Metrics.store(MetricStores.request, "store");
            // exception
            Metrics.exception(new RuntimeException());
            // object
            Metrics.object(new Object());
        });

        Map<String, String> index = Metrics.showIndexs();
        System.out.println(GSON.toJson(index));
        checkValue(() -> {
            Assert.assertEquals("index", index.get(MetricIndexs.metric_type.name()));
            Assert.assertNotNull(index.get(MetricIndexs.exception_type.name()));
        });

        Map<String, String> store = Metrics.showStores();
        System.out.println(GSON.toJson(store));
        checkValue(() -> {
            Assert.assertEquals("store", store.get(MetricStores.request.name()));
            Assert.assertNotNull(store.get(MetricStores.exception.name()));
        });

        Metrics.build();
    }

    @Test
    public void metric2() {
        // Option 2: add metric by automatic placeholder "$"
        Metrics.Log().of(
                // index
                $(MetricIndexs.metric_type, "index"),
                // store
                $(MetricStores.request, "store"),
                // exception
                $(new RuntimeException()),
                // object
                $(new Object())
        );

        Map<String, String> index = Metrics.showIndexs();
        System.out.println(GSON.toJson(index));
        checkValue(() -> {
            Assert.assertEquals("index", index.get(MetricIndexs.metric_type.name()));
            Assert.assertNotNull(index.get(MetricIndexs.exception_type.name()));
        });

        Map<String, String> store = Metrics.showStores();
        System.out.println(GSON.toJson(store));
        checkValue(() -> {
            Assert.assertEquals("store", store.get(MetricStores.request.name()));
            Assert.assertNotNull(store.get(MetricStores.exception.name()));
        });

        Metrics.build();
    }

    @Test
    public void metric3() {
        // Also you can use {@code local()} to start a local scope
        String local = Metrics.local();
        Metrics.Log(local).of(
                // Also you can use supplier
                () -> $(MetricIndexs.metric_type, "index"),
                () -> $(MetricStores.request, "store"),
                () -> $(new RuntimeException()),
                () -> $(new Object())
        );

        Map<String, String> index = Metrics.showIndexs(local);
        System.out.println(GSON.toJson(index));
        checkValue(() -> {
            Assert.assertEquals("index", index.get(MetricIndexs.metric_type.name()));
            Assert.assertNotNull(index.get(MetricIndexs.exception_type.name()));
        });

        Map<String, String> store = Metrics.showStores(local);
        System.out.println(GSON.toJson(store));
        checkValue(() -> {
            Assert.assertEquals("store", store.get(MetricStores.request.name()));
            Assert.assertNotNull(store.get(MetricStores.exception.name()));
        });

        Metrics.build(local);
    }
}