package com.ten.func.vavr.metric;

import com.google.gson.Gson;
import com.ten.func.vavr.core.config.SpringContextConfigurator;
import io.vavr.API;
import io.vavr.CheckedRunnable;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

/**
 * Metric module
 *
 * @author shihaowang
 * @date 2020/1/1
 */
@Slf4j
public final class Metric {

    private static final Gson GSON = new Gson();

    /**
     * Configurator
     */
    private static MetricConfiguration configuration;

    static {
        try {
            configuration = SpringContextConfigurator.getBean(MetricConfiguration.class);
        } catch (Throwable throwable) {
            log.error("[vavr-work] metric init spring context configuration failure");
        }
        if (configuration == null) {
            configuration = new MetricConfiguration(
                    "DEFAULT-TOPIC",
                    (throwable, indexMap, storeMap) -> {
                        log.warn("[vavr-work] metric default metric exception", throwable);
                        return null;
                    },
                    (topic, indexMap, storeMap) -> {
                        log.info("[vavr-work] metric default metric tags index[{}] store[{}]", GSON.toJson(indexMap), GSON.toJson(storeMap));
                        return null;
                    });
        }
    }

    private Metric() {
    }

    /**
     * Thread local cache params map
     */
    private static final ThreadLocal<Map<String, String>> INDEX_TAG = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, String>> STORED_TAG = ThreadLocal.withInitial(HashMap::new);
    /**
     * Thread local cache multi params map
     */
    private static final ThreadLocal<Map<String, Map<String, String>>> KEY_INDEX_TAG = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Map<String, String>>> KEY_STORED_TAG = ThreadLocal.withInitial(HashMap::new);

    // -------------------------------------------------------------- metric area

    public static void metric(CheckedRunnable runnable) {
        runChecked(runnable, configuration.isMetricSwitch());
    }

    public static void debug(CheckedRunnable runnable) {
        runChecked(runnable, configuration.isMetricDebugSwitch());
    }

    /**
     * Metric runnable with switch from configuration
     */
    private static void runChecked(CheckedRunnable runnable, boolean switchFlag) {
        if (switchFlag) {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                log.warn("[vavr-work] metric runnable exception", throwable);
            }
        }
    }

    // -------------------------------------------------------------- thread local key

    /**
     * Create a new params map at thread local which marked by {@code key}
     */
    public static String startMetric() {
        final String key = String.valueOf(System.nanoTime());
        KEY_STORED_TAG.get().put(key, new HashMap<>());
        KEY_INDEX_TAG.get().put(key, new HashMap<>());
        return key;
    }

    // -------------------------------------------------------------- $ placeholder metric

    /**
     * Entry point of the tags API.
     *
     * @param key thread local multi metric key
     * @return a new {@code Tags} instance
     */
    public static Tags Log(String... key) {
        if (key == null || key.length == 0) {
            return new Tags(Option.none());
        } else {
            return new Tags(Option.of(key[0]));
        }
    }

    /**
     * Placeholder which used in {@code Log} area
     */
    public static Option<Tags.ItemCase> $(MetricItem pattern, Object value) {
        Objects.requireNonNull(pattern, "[vavr-work] metric pattern is null");
        Objects.requireNonNull(value, "[vavr-work] metric value is null");
        return Match(pattern).option(
                Case(API.$(instanceOf(MetricIndex.class)), clazz -> new Tags.IndexCase(pattern, () -> value)),
                Case(API.$(instanceOf(MetricStore.class)), clazz -> new Tags.StoreCase(pattern, () -> value))
        );
    }

    /**
     * Placeholder which used in {@code Log} area
     */
    public static Option<Tags.ItemCase> $(MetricItem pattern, Supplier<?> value) {
        Objects.requireNonNull(pattern, "[vavr-work] metric pattern is null");
        Objects.requireNonNull(value, "[vavr-work] metric value is null");
        return Match(pattern).option(
                Case(API.$(instanceOf(MetricIndex.class)), clazz -> new Tags.IndexCase(pattern, value)),
                Case(API.$(instanceOf(MetricStore.class)), clazz -> new Tags.StoreCase(pattern, value))
        );
    }

    /**
     * Instances are obtained via {@link Metric#Log(String...)}.
     */
    public static final class Tags {

        /**
         * Thread local cache multi params map key
         */
        private final Option<String> key;

        private Tags(Option<String> key) {
            this.key = key;
        }

        /**
         * Obtained via {@link Metric#$(MetricItem, Supplier)}
         */
        @SafeVarargs
        @SuppressWarnings({"varargs"})
        public final void of(Option<ItemCase>... caseOps) {
            for (Option<ItemCase> caseOp : caseOps) {
                caseOp.toJavaOptional().ifPresent(itemCase -> itemCase.accept(key));
            }
        }

        // -- CASES

        public interface ItemCase extends Consumer<Option<String>> {
        }

        @AllArgsConstructor
        public static final class IndexCase implements ItemCase {

            private final MetricIndex pattern;
            private final Supplier<?> value;

            private IndexCase(MetricItem pattern, Supplier<?> value) {
                this.pattern = (MetricIndex) pattern;
                this.value = value;
            }

            @Override
            public void accept(Option<String> key) {
                Metric.metric(() -> {
                    key.map(s -> {
                        metricIndex(s, pattern, value.get());
                        return null;
                    }).orElse(() -> {
                        metricIndex(pattern, value.get());
                        return null;
                    });
                });
            }
        }

        @AllArgsConstructor
        public static final class StoreCase implements ItemCase {

            private final MetricStore pattern;
            private final Supplier<?> value;

            private StoreCase(MetricItem pattern, Supplier<?> value) {
                this.pattern = (MetricStore) pattern;
                this.value = value;
            }

            @Override
            public void accept(Option<String> key) {
                Metric.metric(() -> {
                    key.map(s -> {
                        metricStore(s, pattern, value.get());
                        return null;
                    }).orElse(() -> {
                        metricStore(pattern, value.get());
                        return null;
                    });
                });
            }
        }
    }

    /* metric index */

    public static void metricIndex(MetricIndex metric, String value) {
        if (metric != null) {
            INDEX_TAG.get().put(metric.name(), value);
        }
    }

    public static void metricIndex(MetricIndex metric, Object value) {
        if (metric != null && value != null) {
            INDEX_TAG.get().put(metric.name(), value.toString());
        }
    }

    public static void metricIndex(String key, MetricIndex metric, String value) {
        if (metric != null) {
            final Map<String, String> keyMap = KEY_INDEX_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value);
            }
        }
    }

    public static void metricIndex(String key, MetricIndex metric, Object value) {
        if (metric != null && value != null) {
            final Map<String, String> keyMap = KEY_INDEX_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value.toString());
            }
        }
    }

    public static void metricIndex(Object o) {
        if (o != null) {
            metricObject(o);
        }
    }

    public static void metricIndex(String key, Object o) {
        if (o != null) {
            final Map<String, String> keyMap = KEY_INDEX_TAG.get().get(key);
            if (keyMap != null) {
                metricObject(key, o);
            }
        }
    }

    /* metric store */

    public static void metricStore(MetricStore metric, String value) {
        if (metric != null) {
            STORED_TAG.get().put(metric.name(), value);
        }
    }

    public static void metricStore(MetricStore metric, Object value) {
        if (metric != null && value != null) {
            STORED_TAG.get().put(metric.name(), value.toString());
        }
    }

    public static void metricStore(String key, MetricStore metric, String value) {
        if (metric != null) {
            final Map<String, String> keyMap = KEY_STORED_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value);
            }
        }
    }

    public static void metricStore(String key, MetricStore metric, Object value) {
        if (metric != null && value != null) {
            final Map<String, String> keyMap = KEY_STORED_TAG.get().get(key);
            if (keyMap != null) {
                STORED_TAG.get().put(metric.name(), value.toString());
            }
        }
    }

    /* metric exception */

    public static void metricException(Throwable e) {
        if (e != null) {
            configuration.getMetricExceptionFunc().apply(e, INDEX_TAG.get(), STORED_TAG.get());
        }
    }

    public static void metricException(String key, Throwable e) {
        if (e != null) {
            final Map<String, String> keyMap1 = KEY_INDEX_TAG.get().get(key);
            final Map<String, String> keyMap2 = KEY_STORED_TAG.get().get(key);
            if (keyMap1 != null && keyMap2 != null) {
                configuration.getMetricExceptionFunc().apply(e, keyMap1, keyMap2);
            }
        }
    }

    public static String buildExceptionStack(Throwable cause) {
        if (cause != null) {
            StringWriter stringWriter = new StringWriter(2048);
            cause.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        } else {
            return "";
        }
    }

    /* metric show */

    public static Map<String, String> metricShowIndex() {
        final Map<String, String> index = INDEX_TAG.get();
        return new HashMap<>(index);
    }

    public static Map<String, String> metricShowIndex(String key) {
        final Map<String, String> index = KEY_INDEX_TAG.get().get(key);
        return new HashMap<>(index);
    }

    public static Map<String, String> metricShowStore() {
        final Map<String, String> store = STORED_TAG.get();
        return new HashMap<>(store);
    }

    public static Map<String, String> metricShowStore(String key) {
        final Map<String, String> store = KEY_STORED_TAG.get().get(key);
        return new HashMap<>(store);
    }

    /* metric object */

    private static void metricObject(Object o) {
        metricObject(o, INDEX_TAG.get(), STORED_TAG.get());
    }

    private static void metricObject(String key, Object o) {
        metricObject(o, KEY_INDEX_TAG.get().get(key), KEY_STORED_TAG.get().get(key));
    }

    private static void metricObject(Object o,
                                     Map<String, String> metricIndexMap,
                                     Map<String, String> metricStoreMap) {
        try {
            final Class<?> oClass = o.getClass();
            MetricFields annotation = oClass.getAnnotation(MetricFields.class);
            if (annotation == null) {
                return;
            }

            Field[] fields = FieldUtils.getAllFields(oClass);
            for (Field field : fields) {
                MetricFields fieldAnnotation = field.getAnnotation(MetricFields.class);
                PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), oClass);
                Object value = descriptor.getReadMethod().invoke(o);
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    continue;
                }
                if (fieldAnnotation != null) {
                    if (fieldAnnotation.isMetric()) {
                        if (fieldAnnotation.isIndex()) {
                            metricIndexMap.put(StringUtils.isEmpty(fieldAnnotation.key()) ? field.getName() : fieldAnnotation.key(), String.valueOf(value));
                        } else {
                            metricStoreMap.put(StringUtils.isEmpty(fieldAnnotation.key()) ? field.getName() : fieldAnnotation.key(), String.valueOf(value));
                        }
                    }
                } else {
                    metricIndexMap.put(field.getName(), String.valueOf(value));
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("[vavr-work] metric failed to metric [{}], error[{}]", GSON.toJson(o), e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------- finally

    /**
     * Log tags according to the configuration
     * {@link MetricConfiguration#getLogTagsFunc()}
     */
    public static void logTags() {
        configuration.getLogTagsFunc().apply(configuration.getTopic(), INDEX_TAG.get(), STORED_TAG.get());
        remove();

    }

    /**
     * Log tags according to the configuration
     * {@link MetricConfiguration#getLogTagsFunc()}
     */
    public static void logTags(String key) {
        configuration.getLogTagsFunc().apply(configuration.getTopic(), KEY_INDEX_TAG.get().get(key), KEY_STORED_TAG.get().get(key));
        removeKey(key);
    }

    public static void remove() {
        INDEX_TAG.remove();
        KEY_INDEX_TAG.remove();
        STORED_TAG.remove();
        KEY_STORED_TAG.remove();
    }

    public static void removeKey(String key) {
        KEY_INDEX_TAG.get().remove(key);
        KEY_STORED_TAG.get().remove(key);
    }
}
