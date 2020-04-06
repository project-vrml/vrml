package com.ten.func.vavr.metric;

import com.google.gson.Gson;
import com.ten.func.vavr.core.beans.SpringContextConfigurator;
import com.ten.func.vavr.core.tags.Important;
import com.ten.func.vavr.metric.config.MetricConfiguration;
import com.ten.func.vavr.metric.index.MetricIndex;
import com.ten.func.vavr.metric.index.MetricIndexs;
import com.ten.func.vavr.metric.store.MetricStore;
import com.ten.func.vavr.metric.store.MetricStores;
import io.vavr.API;
import io.vavr.CheckedRunnable;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

/**
 * Metric module API.
 */
@Slf4j
public final class Metrics {

    /**
     * Recommend initial map size
     */
    private static final int RECOMMEND_INDEX_SIZE = 32;
    private static final int RECOMMEND_STORE_SIZE = 8;

    /**
     * Formatting tool
     */
    private static final Gson GSON = new Gson();

    /**
     * Async metrics switch
     */
    private static boolean asyncLogging = false;

    /**
     * Async metrics executor
     */
    private static ExecutorService executor = null;

    /**
     * Metrics configurator
     */
    private static MetricConfiguration configuration;

    private static final Object INIT_LOCK = new Object();

    private static void initSpringContextConfig() {
        if (configuration == null) {
            synchronized (INIT_LOCK) {
                if (configuration == null) {
                    // load metrics configuration from spring context
                    try {
                        configuration = SpringContextConfigurator.getBean(MetricConfiguration.class);
                    } catch (Throwable throwable) {
                        log.error("Metrics init spring context configuration failure.", throwable);
                    }
                }
            }
        }
    }

    @Important(description = "The only way to get configuration")
    private static MetricConfiguration getConfiguration() {
        Metrics.initSpringContextConfig();
        return configuration;
    }

    /**
     * API demo show
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new Metrics((index, store) -> {
            System.out.println(GSON.toJson(index));
            System.out.println(GSON.toJson(store));
        });
    }

    /**
     * API demo show
     */
    private Metrics(BiConsumer<Map<String, String>, Map<String, String>> howToShow) {
        //  Option 1: add metric manually
        Metrics.metric(() -> {
            // index
            index(MetricIndexs.metric_type, "index");
            // store
            store(MetricStores.request, "store");
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
                $(MetricStores.request, "store"),
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
                () -> $(MetricStores.request, "store"),
                () -> $(new RuntimeException()),
                () -> $(new Object())
        );
        howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
        Metrics.build(local);
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

    /*
     * Option 1: add metric manually
     */

    /**
     * Metric with runnable lambda
     *
     * @param runnable the runnable
     */
    public static void metric(CheckedRunnable runnable) {
        call(runnable, getConfiguration().metricSwitch());
    }

    /**
     * Metric debug with runnable lambda
     *
     * @param runnable the runnable
     */
    public static void debug(CheckedRunnable runnable) {
        call(runnable, getConfiguration().debugSwitch());
    }

    /**
     * Metric runnable with switch from configuration
     */
    private static void call(CheckedRunnable runnable, boolean switchOpen) {
        if (switchOpen) {
            if (asyncLogging) {
                executor.submit(() -> runChecked(runnable));
            } else {
                runChecked(runnable);
            }
        }
    }

    private static void runChecked(CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            log.warn("Metrics runnable exception", throwable);
        }
    }

    /*
     * Option 2: add metric by automatic placeholder
     */

    // -- Local scope key

    /**
     * Create a new params map at thread local which marked by {@code key}
     *
     * @return the string
     */
    public static String local() {
        final String key = String.valueOf(System.nanoTime());
        KEY_INDEX_TAG.get().put(key, new HashMap<>(RECOMMEND_INDEX_SIZE));
        KEY_STORED_TAG.get().put(key, new HashMap<>(RECOMMEND_STORE_SIZE));
        return key;
    }

    // -- Entry point with lambda "() -> { scope }"

    /**
     * Entry point of the tags API.
     *
     * @param key thread local multi metric key
     * @return a new {@code Tags} instance
     */
    public static Tags Log(String key) {
        Metrics.initSpringContextConfig();
        return new Tags(Option.of(key));
    }

    /**
     * Entry point of the tags thread local API.
     *
     * @return a new {@code Tags} instance
     */
    public static Tags Log() {
        Metrics.initSpringContextConfig();
        return new Tags(Option.none());
    }

    // -- Entry point with "$" in the lambda scope

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param pattern the pattern
     * @param value   the value
     * @return the option
     */
    public static Option<Tags.ItemCase> $(MetricItem pattern, Object value) {
        Objects.requireNonNull(pattern, "Metrics pattern is null");
        Objects.requireNonNull(value, "Metrics value is null");
        return Match(pattern).option(
                Case(API.$(instanceOf(MetricIndex.class)), clazz -> new Tags.IndexCase(pattern, () -> value)),
                Case(API.$(instanceOf(MetricStore.class)), clazz -> new Tags.StoreCase(pattern, () -> value))
        );
    }

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param pattern the pattern
     * @param value   the value
     * @return the option
     */
    public static Option<Tags.ItemCase> $(MetricItem pattern, Supplier<?> value) {
        Objects.requireNonNull(pattern, "Metrics pattern is null");
        Objects.requireNonNull(value, "Metrics value is null");
        return Match(pattern).option(
                Case(API.$(instanceOf(MetricIndex.class)), clazz -> new Tags.IndexCase(pattern, value)),
                Case(API.$(instanceOf(MetricStore.class)), clazz -> new Tags.StoreCase(pattern, value))
        );
    }

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param throwable the throwable
     * @return the option
     */
    public static Option<Tags.ItemCase> $(Throwable throwable) {
        Objects.requireNonNull(throwable, "Metrics throwable is null");
        return Option.of(new Tags.ExceptionCase(throwable));
    }

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param object the object
     * @return the option
     */
    public static Option<Tags.ItemCase> $(Object object) {
        Objects.requireNonNull(object, "Metrics object is null");
        return Option.of(new Tags.ObjCase(object));
    }

    // -------------------------------------------------------------- Function

    /**
     * Instances are obtained via {@link Metrics#Log(String)}.
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
         * Obtained via {@link Metrics#$(MetricItem, Supplier)}
         *
         * @param caseOps the case ops
         */
        @SafeVarargs
        @SuppressWarnings({"varargs"})
        public final void of(Option<ItemCase>... caseOps) {
            for (Option<ItemCase> caseOp : caseOps) {
                caseOp.toJavaOptional().ifPresent(itemCase -> itemCase.accept(key));
            }
        }

        /**
         * Obtained via {@link Metrics#$(MetricItem, Supplier)}
         *
         * @param caseOps the case ops
         */
        @SafeVarargs
        @SuppressWarnings({"varargs"})
        public final void of(Supplier<Option<ItemCase>>... caseOps) {
            for (Supplier<Option<ItemCase>> caseOp : caseOps) {
                caseOp.get().toJavaOptional().ifPresent(itemCase -> itemCase.accept(key));
            }
        }

        // -- CASES

        /**
         * The interface Item case.
         */
        public interface ItemCase extends Consumer<Option<String>> {
        }

        /**
         * Metric {@code index}
         */
        public static final class IndexCase implements ItemCase {

            private final MetricIndex pattern;
            private final Supplier<?> value;

            private IndexCase(MetricItem pattern, Supplier<?> value) {
                this.pattern = (MetricIndex) pattern;
                this.value = value;
            }

            @Override
            public void accept(Option<String> key) {
                Metrics.metric(() -> {
                    key.map(_key -> {
                        index(_key, pattern, value.get());
                        return null;
                    }).orElse(() -> {
                        index(pattern, value.get());
                        return null;
                    });
                });
            }
        }

        /**
         * Metric {@code store}
         */
        public static final class StoreCase implements ItemCase {

            private final MetricStore pattern;
            private final Supplier<?> value;

            private StoreCase(MetricItem pattern, Supplier<?> value) {
                this.pattern = (MetricStore) pattern;
                this.value = value;
            }

            @Override
            public void accept(Option<String> key) {
                Metrics.metric(() -> {
                    key.map(_key -> {
                        store(_key, pattern, value.get());
                        return null;
                    }).orElse(() -> {
                        store(pattern, value.get());
                        return null;
                    });
                });
            }
        }

        /**
         * Metric {@code exception}
         */
        public static final class ExceptionCase implements ItemCase {

            private final Throwable throwable;

            private ExceptionCase(Throwable throwable) {
                this.throwable = throwable;
            }

            @Override
            public void accept(Option<String> key) {
                Metrics.metric(() -> {
                    key.map(_key -> {
                        exception(_key, throwable);
                        return null;
                    }).orElse(() -> {
                        exception(throwable);
                        return null;
                    });
                });
            }
        }

        /**
         * Metric {@code object} with {@link Metric} annotation
         */
        public static final class ObjCase implements ItemCase {

            private final Object object;

            private ObjCase(Object object) {
                this.object = object;
            }

            @Override
            public void accept(Option<String> key) {
                Metrics.metric(() -> {
                    key.map(_key -> {
                        object(_key, object);
                        return null;
                    }).orElse(() -> {
                        object(object);
                        return null;
                    });
                });
            }
        }
    }

    // -- Metric Index

    /**
     * Index.
     *
     * @param metric the metric
     * @param value  the value
     */
    public static void index(MetricIndex metric, String value) {
        if (metric != null) {
            INDEX_TAG.get().put(metric.name(), value);
        }
    }

    /**
     * Index.
     *
     * @param metric the metric
     * @param value  the value
     */
    public static void index(MetricIndex metric, Object value) {
        if (metric != null && value != null) {
            INDEX_TAG.get().put(metric.name(), value.toString());
        }
    }

    /**
     * Index.
     *
     * @param key    the key
     * @param metric the metric
     * @param value  the value
     */
    public static void index(String key, MetricIndex metric, String value) {
        if (metric != null) {
            final Map<String, String> keyMap = KEY_INDEX_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value);
            }
        }
    }

    /**
     * Index.
     *
     * @param key    the key
     * @param metric the metric
     * @param value  the value
     */
    public static void index(String key, MetricIndex metric, Object value) {
        if (metric != null && value != null) {
            final Map<String, String> keyMap = KEY_INDEX_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value.toString());
            }
        }
    }

    // -- Metric Store

    /**
     * Store.
     *
     * @param metric the metric
     * @param value  the value
     */
    public static void store(MetricStore metric, String value) {
        if (metric != null) {
            STORED_TAG.get().put(metric.name(), value);
        }
    }

    /**
     * Store.
     *
     * @param metric the metric
     * @param value  the value
     */
    public static void store(MetricStore metric, Object value) {
        if (metric != null && value != null) {
            STORED_TAG.get().put(metric.name(), value.toString());
        }
    }

    /**
     * Store.
     *
     * @param key    the key
     * @param metric the metric
     * @param value  the value
     */
    public static void store(String key, MetricStore metric, String value) {
        if (metric != null) {
            final Map<String, String> keyMap = KEY_STORED_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value);
            }
        }
    }

    /**
     * Store.
     *
     * @param key    the key
     * @param metric the metric
     * @param value  the value
     */
    public static void store(String key, MetricStore metric, Object value) {
        if (metric != null && value != null) {
            final Map<String, String> keyMap = KEY_STORED_TAG.get().get(key);
            if (keyMap != null) {
                keyMap.put(metric.name(), value.toString());
            }
        }
    }

    // -- Metric Exception

    /**
     * Exception.
     *
     * @param e the e
     */
    public static void exception(Throwable e) {
        if (e != null) {
            getConfiguration().metricException().apply(e, INDEX_TAG.get(), STORED_TAG.get());
        }
    }

    /**
     * Exception.
     *
     * @param key the key
     * @param e   the e
     */
    public static void exception(String key, Throwable e) {
        if (e != null) {
            final Map<String, String> keyMap1 = KEY_INDEX_TAG.get().get(key);
            final Map<String, String> keyMap2 = KEY_STORED_TAG.get().get(key);
            if (keyMap1 != null && keyMap2 != null) {
                getConfiguration().metricException().apply(e, keyMap1, keyMap2);
            }
        }
    }

    // -- Metric Object

    /**
     * Object.
     *
     * @param o the o
     */
    public static void object(Object o) {
        object(o, INDEX_TAG.get(), STORED_TAG.get());
    }

    /**
     * Object.
     *
     * @param key the key
     * @param o   the o
     */
    public static void object(String key, Object o) {
        object(o, KEY_INDEX_TAG.get().get(key), KEY_STORED_TAG.get().get(key));
    }

    private static void object(Object o,
                               Map<String, String> metricIndexMap,
                               Map<String, String> metricStoreMap) {
        try {
            final Class<?> oClass = o.getClass();
            Metric annotation = oClass.getAnnotation(Metric.class);
            if (annotation == null) {
                return;
            }

            Field[] fields = FieldUtils.getAllFields(oClass);
            for (Field field : fields) {
                Metric fieldAnnotation = field.getAnnotation(Metric.class);
                // field annotation
                if (fieldAnnotation != null) {
                    if (fieldAnnotation.isMetric()) {
                        Object value = readValue(o, oClass, field);
                        if (value == null) {
                            continue;
                        }
                        // index metric
                        if (fieldAnnotation.isIndex()) {
                            metricIndexMap.put(StringUtils.isEmpty(fieldAnnotation.key()) ?
                                    field.getName() :
                                    fieldAnnotation.key(), String.valueOf(value));
                        }
                        // store metric
                        else {
                            metricStoreMap.put(StringUtils.isEmpty(fieldAnnotation.key()) ?
                                    field.getName() :
                                    fieldAnnotation.key(), String.valueOf(value));
                        }
                    } else {
                        // ignore field
                    }
                }
                // class annotation
                else {
                    Object value = readValue(o, oClass, field);
                    if (value == null) {
                        continue;
                    }
                    metricIndexMap.put(field.getName(), String.valueOf(value));
                }
            }
        } catch (Exception e) {
            log.error("Metrics failed to metric [{}], error[{}]", GSON.toJson(o), e.getMessage(), e);
        }
    }

    private static Object readValue(Object o, Class<?> oClass, Field field) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), oClass);
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                return null;
            }
            Object value = readMethod.invoke(o);
            if (value == null || StringUtils.isEmpty(value.toString())) {
                return null;
            }
            return value;
        } catch (Exception e) {
            log.warn("Metrics failed to read [{}], error[{}]", field.getName(), e.getMessage(), e);
            return null;
        }
    }

    // -- Finally

    /**
     * Log tags according to the configuration
     * {@link MetricConfiguration#metricFinally()}
     */
    public static void build() {
        getConfiguration().metricFinally().apply(getConfiguration().topic(), INDEX_TAG.get(), STORED_TAG.get());
        remove();
    }

    /**
     * Log tags according to the configuration
     * {@link MetricConfiguration#metricFinally()}
     *
     * @param key the key
     */
    public static void build(String key) {
        getConfiguration().metricFinally().apply(getConfiguration().topic(), KEY_INDEX_TAG.get().get(key), KEY_STORED_TAG.get().get(key));
        remove(key);
    }

    // -- Remove

    /**
     * Remove threadLocal map and all-keys map
     */
    public static void remove() {
        INDEX_TAG.remove();
        KEY_INDEX_TAG.remove();
        STORED_TAG.remove();
        KEY_STORED_TAG.remove();
    }

    /**
     * Remove the-key map
     *
     * @param key the key
     */
    public static void remove(String key) {
        if (KEY_INDEX_TAG.get() != null) {
            KEY_INDEX_TAG.get().remove(key);
        }
        if (KEY_STORED_TAG.get() != null) {
            KEY_STORED_TAG.get().remove(key);
        }
    }

    // -- Show

    /**
     * Show indexs map.
     *
     * @return the map
     */
    public static Map<String, String> showIndexs() {
        final Map<String, String> index = INDEX_TAG.get();
        return new HashMap<>(index);
    }

    /**
     * Show indexs map.
     *
     * @param key the key
     * @return the map
     */
    public static Map<String, String> showIndexs(String key) {
        final Map<String, String> index = KEY_INDEX_TAG.get().get(key);
        return new HashMap<>(index);
    }

    /**
     * Show stores map.
     *
     * @return the map
     */
    public static Map<String, String> showStores() {
        final Map<String, String> store = STORED_TAG.get();
        return new HashMap<>(store);
    }

    /**
     * Show stores map.
     *
     * @param key the key
     * @return the map
     */
    public static Map<String, String> showStores(String key) {
        final Map<String, String> store = KEY_STORED_TAG.get().get(key);
        return new HashMap<>(store);
    }
}
