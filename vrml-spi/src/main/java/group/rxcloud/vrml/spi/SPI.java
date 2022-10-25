package group.rxcloud.vrml.spi;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * The Java Spi API.
 */
public final class SPI {

    private static final Map<String, List> spiImplCache;

    static {
        spiImplCache = new ConcurrentHashMap<>();
    }

    private static <T> List<T> load0(Class<T> spiClass) {
        return spiImplCache.computeIfAbsent(spiClass.getName(),
                s -> JavaSpiLoader.loadJavaSpi(spiClass));
    }

    /**
     * Load spi impl optional.
     *
     * @param <T>      the spi class type
     * @param spiClass the spi class
     * @return the spi impl optional
     */
    public static <T> Optional<T> loadSpiImpl(Class<T> spiClass) {
        List<T> list = load0(spiClass);
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
        List<T> list = load0(spiClass);
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
        return loadSpiImpl(spiClass)
                .orElseGet(defaultImpl);
    }
}
