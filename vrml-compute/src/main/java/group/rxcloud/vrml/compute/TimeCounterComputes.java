package group.rxcloud.vrml.compute;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import group.rxcloud.vrml.compute.config.ComputeConfiguration;
import group.rxcloud.vrml.core.tags.Fixme;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * The Time+Counter computes.
 */
public final class TimeCounterComputes extends Computes<group.rxcloud.vrml.compute.TimeCounterComputes.TimeCounterComputeConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(TimeCounterComputes.class);

    private static final Map<String, TimeCounterCache> TIME_COUNTER_CACHE_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * Singleton.
     */
    TimeCounterComputes() {
    }

    @Override
    public void compute(String key, Runnable f) {
        this.compute(key, null, f);
    }

    @Override
    public void compute(String key, Runnable left, Runnable right) {
        TimeCounterComputeConfig computeConfiguration = getConfiguration()
                .getComputeConfiguration(key);
        Objects.requireNonNull(computeConfiguration, "computeConfiguration");
        Either<Integer, Integer> checkCount = TIME_COUNTER_CACHE_CONCURRENT_HASH_MAP
                .computeIfAbsent(key, key1 -> new TimeCounterCache(key1, computeConfiguration))
                .putAndCheckCount();
        if (checkCount.isLeft()) {
            if (left != null) {
                left.run();
            }
        } else {
            if (right != null) {
                right.run();
            }
        }
    }

    /**
     * The Time+Counter compute configuration.
     * <p>
     * Please impl this interface by your own class.
     */
    public interface TimeCounterComputeConfiguration extends ComputeConfiguration<TimeCounterComputeConfig> {

        @Override
        TimeCounterComputeConfig getComputeConfiguration(String key);
    }

    /**
     * The Time+Counter compute config.
     */
    public static class TimeCounterComputeConfig implements ComputeConfiguration.ComputeConfig {

        /**
         * Unique key
         */
        private String key;
        /**
         * Time expiration seconds
         */
        private Long expirationTime;
        /**
         * Counter trigger count
         */
        private Long triggerCount;

        @Override
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Long getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(Long expirationTime) {
            this.expirationTime = expirationTime;
        }

        public Long getTriggerCount() {
            return triggerCount;
        }

        public void setTriggerCount(Long triggerCount) {
            this.triggerCount = triggerCount;
        }
    }

    private static final class TimeCounterCache {

        private final Object lock = new Object();

        private final String key;
        private final TimeCounterComputeConfig timeCounterComputeConfig;

        private Cache<String, Integer> cache;

        /**
         * Instantiates a new Time counter cache.
         *
         * @param key                      the key
         * @param timeCounterComputeConfig the time counter compute config
         */
        TimeCounterCache(String key, TimeCounterComputeConfig timeCounterComputeConfig) {
            this.key = key;
            this.timeCounterComputeConfig = timeCounterComputeConfig;
            this.initCache();
        }

        /**
         * Use {@link Caffeine} cache to value
         */
        private void initCache() {
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(timeCounterComputeConfig.getExpirationTime(), TimeUnit.SECONDS)
                    .build();
        }

        /**
         * Put the value to statistic pool
         *
         * @return the either
         */
        @Fixme(fixme = "optimize synchronized key")
        Either<Integer, Integer> putAndCheckCount() {
            synchronized (lock) {
                Integer count = cache.getIfPresent(key);
                if (count == null) {
                    // init
                    cache.put(key, 1);
                    return Either.left(1);
                } else {
                    if (count >= timeCounterComputeConfig.getTriggerCount()) {
                        // clear
                        cache.put(key, 1);
                        return Either.right(count);
                    } else {
                        // addition
                        Integer newV = ++count;
                        cache.put(key, newV);
                        return Either.left(newV);
                    }
                }
            }
        }
    }
}
