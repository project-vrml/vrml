package group.rxcloud.vrml.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * The Vrml {@link Flux} utils.
 */
public final class VrFlux {

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
}
