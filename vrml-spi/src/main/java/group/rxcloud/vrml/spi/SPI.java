package group.rxcloud.vrml.spi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * The Java Spi API.
 */
public final class SPI {

    private static final Map<Class, List> spiImplCache;

    static {
        spiImplCache = new ConcurrentHashMap<>();
    }

    /**
     * Load spi impl optional.
     *
     * @param <T>      the spi class type
     * @param spiClass the spi class
     * @return the spi impl optional
     */
    public static <T> Optional<T> loadSpiImpl(Class<T> spiClass) {
        List<T> list = spiImplCache.computeIfAbsent(spiClass,
                aClass -> JavaSpiLoader.loadJavaSpi(spiClass));
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(0));
    }

    /**
     * Load spi impls optional.
     *
     * @param <T>      the spi class type
     * @param spiClass the spi class
     * @return the spi impls optional
     */
    public static <T> Optional<List<T>> loadSpiImpls(Class<T> spiClass) {
        List<T> list = spiImplCache.computeIfAbsent(spiClass,
                aClass -> JavaSpiLoader.loadJavaSpi(spiClass));
        return Optional.of(list);
    }

    /**
     * Load spi impl, return default when null.
     *
     * @param <T>         the spi class type
     * @param spiClass    the spi class
     * @param defaultImpl the default impl
     * @return the spi impl or default impl
     */
    public static <T> T loadSpiImpl(Class<T> spiClass, Supplier<T> defaultImpl) {
        List list = spiImplCache.computeIfAbsent(spiClass,
                aClass -> {
                    Optional<T> t = loadSpiImpl(spiClass);
                    return t.<List>map(Collections::singletonList)
                            .orElseGet(() -> Collections.singletonList(defaultImpl.get()));
                });
        return (T) list.get(0);
    }
}
