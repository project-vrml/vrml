package com.ten.func.vavr.metric;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ten.func.vavr.metric.Metric.$;

@Slf4j
public class MetricTest {

    private static int baseType = 1;
    private static String stringType = "string";
    private static List<String> collectionType = Collections.singletonList("string");
    private static List<String> collectionType2 = Arrays.asList("t", "e", "s", "t");

    public static void main(String[] args) {

        log.info("2" + null);
        String s = "@" + null;

        Metric.Log().of(
                $(MetricIndexTest.test_name, baseType),
                $(MetricIndexTest.test_desc, stringType),
                $(MetricStoreTest.test_name, () -> collectionType),
                $(MetricStoreTest.test_desc, () -> collectionType2)
        );

        Map<String, String> index = Metric.metricShowIndex();
        index.forEach((key, value) -> System.out.println(key + ":" + value));
        Assert.assertEquals(String.valueOf(baseType), index.get(MetricIndexTest.test_name.name()));
        Assert.assertEquals(stringType, index.get(MetricIndexTest.test_desc.name()));

        Map<String, String> store = Metric.metricShowStore();
        store.forEach((key, value) -> System.out.println(key + ":" + value));
        Assert.assertEquals(collectionType.toString(), store.get(MetricStoreTest.test_name.name()));
        Assert.assertEquals(collectionType2.toString(), store.get(MetricStoreTest.test_desc.name()));
    }
}
