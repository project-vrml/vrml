package group.rxcloud.vrml.metric;

import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import group.rxcloud.vrml.core.serialization.Serialization;
import group.rxcloud.vrml.core.tags.Important;
import group.rxcloud.vrml.core.tags.Todo;
import group.rxcloud.vrml.metric.config.MetricConfiguration;
import group.rxcloud.vrml.metric.index.MetricIndex;
import group.rxcloud.vrml.metric.store.MetricStore;
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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
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
     * Async metrics switch, currently unused
     */
    @Todo(todo = "Asynchronously execute closures")
    private static boolean asyncLogging = false;

    /**
     * Async metrics executor, currently unused
     */
    @Todo(todo = "Asynchronously execute closures")
    private static ExecutorService executor = null;

    /**
     * Metrics configurator
     */
    private static volatile MetricConfiguration configuration;

    /**
     * Use spring context to provide dynamic configuration.
     */
    private static void initSpringContextConfig() {
        if (configuration == null) {
            synchronized (Metrics.class) {
                if (configuration == null) {
                    // load metrics configuration from spring context
                    try {
                        configuration = SpringContextConfigurator.getBean(MetricConfiguration.class);
                    } catch (Exception e) {
                        log.error("[Vrml]Metrics init spring context configuration failure.", e);
                    }
                }
            }
        }
    }

    @Important(important = "The only way to get spring configuration. Avoid context not loading.")
    private static MetricConfiguration getConfiguration() {
        Metrics.initSpringContextConfig();
        return configuration;
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
     * @param runnable the metric runnable
     */
    public static void metric(CheckedRunnable runnable) {
        call(runnable, getConfiguration().metricSwitch());
    }

    /**
     * Metric debug with runnable lambda
     *
     * @param runnable the debug runnable
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
            log.warn("[Vrml]Metrics runnable exception!", throwable);
        }
    }

    /*
     * Option 2: add metric by automatic placeholder
     */

    // -- Local scope key

    /**
     * Create a new params map at thread local which marked by {@code key}
     *
     * @return the key
     */
    public static String local() {
        final String key = String.valueOf(System.nanoTime());
        KEY_INDEX_TAG.get().put(key, new HashMap<>(RECOMMEND_INDEX_SIZE));
        KEY_STORED_TAG.get().put(key, new HashMap<>(RECOMMEND_STORE_SIZE));
        return key;
    }

    // -- Entry point with lambda "() -> { scope }"

    /**
     * Entry point of the tags thread local key API.
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
     * @param pattern the {@code MetricItem} pattern
     * @param value   the value
     * @return the option of {@code IndexCase} or {@code StoreCase}
     */
    public static Option<Tags.ItemCase> $(MetricItem pattern, Object value) {
        Objects.requireNonNull(pattern, "Metrics pattern is null!");
        Objects.requireNonNull(value, "Metrics value is null!");
        return Match(pattern).option(
                Case(API.$(instanceOf(MetricIndex.class)), clazz -> new Tags.IndexCase(pattern, () -> value)),
                Case(API.$(instanceOf(MetricStore.class)), clazz -> new Tags.StoreCase(pattern, () -> value))
        );
    }

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param pattern the {@code MetricItem} pattern
     * @param value   the value supplier
     * @return the option of {@code IndexCase} or {@code StoreCase}
     * Avoid exceptions that may be thrown by expressions,
     * use lazy evaluation by {@code Supplier} to catch possible exceptions.
     */
    public static Option<Tags.ItemCase> $(MetricItem pattern, Supplier<?> value) {
        Objects.requireNonNull(pattern, "Metrics pattern is null!");
        Objects.requireNonNull(value, "Metrics value supplier is null!");
        return Match(pattern).option(
                Case(API.$(instanceOf(MetricIndex.class)), clazz -> new Tags.IndexCase(pattern, value)),
                Case(API.$(instanceOf(MetricStore.class)), clazz -> new Tags.StoreCase(pattern, value))
        );
    }

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param throwable the metric  throwable
     * @return the option of {@code ExceptionCase}
     */
    public static Option<Tags.ItemCase> $(Throwable throwable) {
        Objects.requireNonNull(throwable, "Metrics throwable is null!");
        return Option.of(new Tags.ExceptionCase(throwable));
    }

    /**
     * Placeholder which used in {@code Log} area
     *
     * @param object the metric object
     * @return the option of {@code ObjCase}
     */
    public static Option<Tags.ItemCase> $(Object object) {
        Objects.requireNonNull(object, "Metrics object is null!");
        return Option.of(new Tags.ObjCase(object));
    }

    // -- Function

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

        /**
         * Use chained calls to end the metric.
         */
        public final void build() {
            Metrics.metric(() -> {
                key.map(_key -> {
                    Metrics.build(_key);
                    return null;
                }).orElse(() -> {
                    Metrics.build();
                    return null;
                });
            });
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
         * Metric {@code throwable}
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
     * Index metric.
     *
     * @param metric the metric key
     * @param value  the value
     */
    public static void index(MetricIndex metric, String value) {
        if (metric != null) {
            INDEX_TAG.get().put(metric.name(), value);
        }
    }

    /**
     * Index metric.
     *
     * @param metric the metric key
     * @param value  the value
     */
    public static void index(MetricIndex metric, Object value) {
        if (metric != null && value != null) {
            INDEX_TAG.get().put(metric.name(), value.toString());
        }
    }

    /**
     * Index metric.
     *
     * @param key    the key
     * @param metric the metric key
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
     * Index metric.
     *
     * @param key    the key
     * @param metric the metric key
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
     * Store metric.
     *
     * @param metric the metric key
     * @param value  the value
     */
    public static void store(MetricStore metric, String value) {
        if (metric != null) {
            STORED_TAG.get().put(metric.name(), value);
        }
    }

    /**
     * Store metric.
     *
     * @param metric the metric key
     * @param value  the value
     */
    public static void store(MetricStore metric, Object value) {
        if (metric != null && value != null) {
            STORED_TAG.get().put(metric.name(), value.toString());
        }
    }

    /**
     * Store metric.
     *
     * @param key    the key
     * @param metric the metric key
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
     * Store metric.
     *
     * @param key    the key
     * @param metric the metric key
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
     * Exception metric.
     *
     * @param throwable the throwable
     */
    public static void exception(Throwable throwable) {
        if (throwable != null) {
            getConfiguration().metricException()
                    .apply(throwable, INDEX_TAG.get(), STORED_TAG.get());
        }
    }

    /**
     * Exception metric.
     *
     * @param key       the key
     * @param throwable the throwable
     */
    public static void exception(String key, Throwable throwable) {
        if (throwable != null) {
            final Map<String, String> keyMap1 = KEY_INDEX_TAG.get().get(key);
            final Map<String, String> keyMap2 = KEY_STORED_TAG.get().get(key);
            if (keyMap1 != null && keyMap2 != null) {
                getConfiguration().metricException()
                        .apply(throwable, keyMap1, keyMap2);
            }
        }
    }

    // -- Metric Object

    /**
     * Object metric.
     *
     * @param o the metric obj
     */
    public static void object(Object o) {
        object(o, INDEX_TAG.get(), STORED_TAG.get());
    }

    /**
     * Object metric.
     *
     * @param key the key
     * @param o   the metric obj
     */
    public static void object(String key, Object o) {
        object(o, KEY_INDEX_TAG.get().get(key), KEY_STORED_TAG.get().get(key));
    }

    /**
     * Metric the obj's all fields if not marked by {@code @Metric(isMetric = false)}.
     *
     * @param o              metric obj
     * @param metricIndexMap the index map values
     * @param metricStoreMap the store map values
     */
    private static void object(Object o,
                               Map<String, String> metricIndexMap,
                               Map<String, String> metricStoreMap) {
        try {
            final Class<?> oClass = o.getClass();
            Metric annotation = oClass.getAnnotation(Metric.class);
            if (annotation == null) {
                return;
            }

            // record the obj's all fields
            Field[] fields = FieldUtils.getAllFields(oClass);
            for (Field field : fields) {
                Metric fieldAnnotation = field.getAnnotation(Metric.class);
                // field annotation
                if (fieldAnnotation != null) {
                    // record field
                    if (fieldAnnotation.isMetric()) {
                        readValue(o, oClass, field)
                                .ifPresent(value -> {
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
                                });
                    }
                    // ignore field
                }
                // class annotation
                else {
                    readValue(o, oClass, field)
                            .ifPresent(value -> {
                                metricIndexMap.put(field.getName(), String.valueOf(value));
                            });
                }
            }
        } catch (Exception e) {
            log.error("Metrics failed to metric [{}], error[{}]", Serialization.toJsonSafe(o), e.getMessage(), e);
        }
    }

    /**
     * Using reflection read method to read obj's metric field value.
     *
     * @param o      metric obj
     * @param oClass metric obj class type
     * @param field  metric obj's field
     * @return obj's metric field value
     */
    private static Optional<Object> readValue(Object o, Class<?> oClass, Field field) {
        try {
            // reflection read method
            PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), oClass);
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                return Optional.empty();
            }

            // invoke read method
            Object value = readMethod.invoke(o);
            if (value == null || StringUtils.isEmpty(value.toString())) {
                return Optional.empty();
            }

            return Optional.of(value);
        } catch (Exception e) {
            log.warn("[Vrml]Metrics failed to read [{}], error[{}]", field.getName(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    // -- Finally

    /**
     * Log tags according to the configuration
     * {@link MetricConfiguration#metricFinally()}
     */
    public static void build() {
        try {
            getConfiguration().metricFinally()
                    .apply(getConfiguration().topic(), INDEX_TAG.get(), STORED_TAG.get());
        } finally {
            remove();
        }
    }

    /**
     * Log all key tags according to the configuration
     * {@link MetricConfiguration#metricFinally()}
     */
    public static void buildKeys() {
        Map<String, Map<String, String>> keyMap = KEY_INDEX_TAG.get();
        keyMap.forEach((key, value) -> build(key));
    }

    /**
     * Log tags according to the configuration
     * {@link MetricConfiguration#metricFinally()}
     *
     * @param key the key
     */
    public static void build(String key) {
        try {
            getConfiguration().metricFinally()
                    .apply(getConfiguration().topic(), KEY_INDEX_TAG.get().get(key), KEY_STORED_TAG.get().get(key));
        } finally {
            remove(key);
        }
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
