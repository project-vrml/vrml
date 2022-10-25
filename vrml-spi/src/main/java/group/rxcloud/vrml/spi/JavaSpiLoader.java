package group.rxcloud.vrml.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * The Java Spi loader.
 */
final class JavaSpiLoader {

    /**
     * Load java spi list.
     *
     * @param <T>      the type parameter
     * @param spiClass the spi class
     * @return the impl list
     */
    static <T> List<T> loadJavaSpi(Class<T> spiClass) {
        Objects.requireNonNull(spiClass, "spiClass is null.");
        try {
            ServiceLoader<T> loader = ServiceLoader.load(spiClass);
            Iterator<T> iterator = loader.iterator();
            if (!iterator.hasNext()) {
                return Collections.emptyList();
            }
            List<T> objs = new ArrayList<>(1);
            while (iterator.hasNext()) {
                T obj = iterator.next();
                objs.add(obj);
            }
            return objs;
        } catch (Exception e) {
            throw new IllegalArgumentException(spiClass.getName() + " spiClass not found.", e);
        }
    }
}
