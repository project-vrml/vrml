package group.rxcloud.vrml.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * The Vrml {@link Flux} utils.
 */
public final class VrFlux {

    private static final Logger log = LoggerFactory.getLogger(VrFlux.class);

    /**
     * Flux: Subscribe after init.
     *
     * @param <T>      the type parameter
     * @param flux     the flux
     * @param consumer the consumer
     */
    public static <T> void subscribeAfterInit(Flux<T> flux, Consumer<? super T> consumer) {
        T t = flux.blockFirst();
        consumer.accept(t);
        flux.subscribe(consumer);
    }

    /**
     * Flux: Subscribe after init.
     *
     * @param <T>       the type parameter
     * @param flux      the flux
     * @param firstLoad the first load timeout
     * @param consumer  the consumer
     */
    public static <T> void subscribeAfterInit(Flux<T> flux, Duration firstLoad, Consumer<? super T> consumer) {
        T t = flux.blockFirst(firstLoad);
        consumer.accept(t);
        flux.subscribe(consumer);
    }

    /**
     * 非必须的
     */
    public static <T> void subscribeAfterInitNonEssential(Flux<T> flux, Consumer<? super T> consumer) {
        T t = null;
        try {
            t = flux.blockFirst();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        consumer.accept(t);
        flux.subscribe(consumer);
    }
}
