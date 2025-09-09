package group.rxcloud.vrml.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.core.AbstractCacheOperations;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Caffeine本地缓存操作实现
 *
 * @author VRML Team
 * @since 1.2.0
 */
public class CaffeineCacheOperations extends AbstractCacheOperations {

    private final Cache<String, CacheValue> cache;
    private final ConcurrentMap<String, Long> expirationTimes = new ConcurrentHashMap<>();

    /**
     * 缓存值包装类，支持TTL
     */
    private static class CacheValue {
        private final Object value;
        private final long expirationTime;

        public CacheValue(Object value, Duration ttl) {
            this.value = value;
            this.expirationTime = ttl != null && !ttl.isNegative() && !ttl.isZero()
                    ? System.currentTimeMillis() + ttl.toMillis()
                    : -1; // -1表示永不过期
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired() {
            return expirationTime > 0 && System.currentTimeMillis() > expirationTime;
        }

        public Duration getRemainingTtl() {
            if (expirationTime <= 0) {
                return Duration.ofSeconds(-1); // 永不过期
            }
            long remaining = expirationTime - System.currentTimeMillis();
            return remaining > 0 ? Duration.ofMillis(remaining) : Duration.ofSeconds(-2); // 已过期
        }
    }

    /**
     * 构造函数
     *
     * @param pattern 缓存键模式
     * @param config  缓存配置
     */
    public CaffeineCacheOperations(String pattern, CacheConfiguration config) {
        super(pattern, config);
        this.cache = buildCache(config);
    }

    /**
     * 复制构造函数
     *
     * @param other 其他实例
     */
    private CaffeineCacheOperations(CaffeineCacheOperations other) {
        super(other);
        this.cache = other.cache; // 共享缓存实例
    }

    @Override
    protected AbstractCacheOperations createCopy() {
        return new CaffeineCacheOperations(this);
    }

    /**
     * 构建Caffeine缓存
     *
     * @param config 缓存配置
     * @return Caffeine缓存实例
     */
    private Cache<String, CacheValue> buildCache(CacheConfiguration config) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        // 设置最大大小
        if (config.getMaxSize() > 0) {
            builder.maximumSize(config.getMaxSize());
        }

        // 启用统计
        if (config.isMetricsEnabled()) {
            builder.recordStats();
        }

        // 设置过期策略 - 写入后过期
        if (config.getDefaultTtl() != null && !config.getDefaultTtl().isNegative()) {
            builder.expireAfterWrite(config.getDefaultTtl());
        }

        // 设置移除监听器
        builder.removalListener((key, value, cause) -> {
            if (key != null) {
                expirationTimes.remove(key);
                log(LogLevel.DEBUG, "Cache entry removed: key={}, cause={}", key, cause);
            }
        });

        return builder.build();
    }

    @Override
    protected <T> Try<Option<T>> doGet(String key, Class<T> valueType) {
        return Try.of(() -> {
            CacheValue cacheValue = cache.getIfPresent(key);
            if (cacheValue == null) {
                return Option.none();
            }

            // 检查是否过期
            if (cacheValue.isExpired()) {
                cache.invalidate(key);
                expirationTimes.remove(key);
                return Option.none();
            }

            Object value = cacheValue.getValue();
            if (value == null) {
                return Option.none();
            }

            // 类型转换
            T typedValue = convertValue(value, valueType);
            return Option.<T>of(typedValue);
        });
    }

    @Override
    protected Try<Void> doPut(String key, Object value, Duration ttl) {
        return Try.run(() -> {
            CacheValue cacheValue = new CacheValue(value, ttl);
            cache.put(key, cacheValue);

            // 记录过期时间
            if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
                expirationTimes.put(key, System.currentTimeMillis() + ttl.toMillis());
            } else {
                expirationTimes.remove(key);
            }
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to put cache value for key: {}", key, throwable);
            return null;
        });
    }

    @Override
    protected Try<Boolean> doEvict(String key) {
        return Try.of(() -> {
            CacheValue existing = cache.getIfPresent(key);
            cache.invalidate(key);
            expirationTimes.remove(key);
            return existing != null;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to evict cache for key: {}", key, throwable);
            return false;
        });
    }

    @Override
    protected Try<Long> doEvictAll(List<String> keys) {
        return Try.of(() -> {
            if (keys.isEmpty()) {
                return 0L;
            }

            long count = 0;
            for (String key : keys) {
                if (cache.getIfPresent(key) != null) {
                    count++;
                }
            }

            cache.invalidateAll(keys);
            keys.forEach(expirationTimes::remove);

            return count;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to evict cache for keys: {}", keys, throwable);
            return 0L;
        });
    }

    @Override
    protected Try<Long> doEvictByPattern(String keyPattern) {
        return Try.of(() -> {
            // 将通配符模式转换为正则表达式
            String regex = keyPattern.replace("*", ".*").replace("?", ".");

            Set<String> keysToRemove = cache.asMap().keySet().stream()
                    .filter(key -> key.matches(regex))
                    .collect(Collectors.toSet());

            cache.invalidateAll(keysToRemove);
            keysToRemove.forEach(expirationTimes::remove);

            return (long) keysToRemove.size();
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to evict cache by pattern: {}", keyPattern, throwable);
            return 0L;
        });
    }

    @Override
    protected Try<Boolean> doExists(String key) {
        return Try.of(() -> {
            CacheValue cacheValue = cache.getIfPresent(key);
            if (cacheValue == null) {
                return false;
            }

            // 检查是否过期
            if (cacheValue.isExpired()) {
                cache.invalidate(key);
                expirationTimes.remove(key);
                return false;
            }

            return true;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to check cache existence for key: {}", key, throwable);
            return false;
        });
    }

    @Override
    protected Try<Boolean> doExpire(String key, Duration ttl) {
        return Try.of(() -> {
            CacheValue existing = cache.getIfPresent(key);
            if (existing == null) {
                return false;
            }

            // 重新设置带新TTL的值
            CacheValue newValue = new CacheValue(existing.getValue(), ttl);
            cache.put(key, newValue);

            // 更新过期时间记录
            if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
                expirationTimes.put(key, System.currentTimeMillis() + ttl.toMillis());
            } else {
                expirationTimes.remove(key);
            }

            return true;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to set expiration for key: {}", key, throwable);
            return false;
        });
    }

    @Override
    protected Try<Duration> doGetTtl(String key) {
        return Try.of(() -> {
            CacheValue cacheValue = cache.getIfPresent(key);
            if (cacheValue == null) {
                return Duration.ofSeconds(-2); // 键不存在
            }

            return cacheValue.getRemainingTtl();
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to get TTL for key: {}", key, throwable);
            return Duration.ofSeconds(-2);
        });
    }

    @Override
    protected <T> Try<Map<String, T>> doMultiGet(List<String> keys, Class<T> valueType) {
        return Try.of(() -> {
            if (keys.isEmpty()) {
                return new HashMap<>();
            }

            Map<String, T> result = new HashMap<>();

            for (String key : keys) {
                Try<Option<T>> valueResult = doGet(key, valueType);
                if (valueResult.isSuccess() && valueResult.get().isDefined()) {
                    result.put(key, valueResult.get().get());
                }
            }

            return result;
        });
    }

    @Override
    protected Try<Void> doMultiPut(Map<String, Object> keyValues, Duration ttl) {
        return Try.run(() -> {
            if (keyValues.isEmpty()) {
                return;
            }

            Map<String, CacheValue> cacheValues = keyValues.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new CacheValue(entry.getValue(), ttl)
                    ));

            cache.putAll(cacheValues);

            // 更新过期时间记录
            if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
                long expirationTime = System.currentTimeMillis() + ttl.toMillis();
                keyValues.keySet().forEach(key -> expirationTimes.put(key, expirationTime));
            } else {
                keyValues.keySet().forEach(expirationTimes::remove);
            }
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to multi-put cache values", throwable);
            return null;
        });
    }

    @Override
    protected Try<Void> doClear() {
        return Try.run(() -> {
            cache.invalidateAll();
            expirationTimes.clear();
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to clear cache", throwable);
            return null;
        });
    }

    @Override
    protected Try<group.rxcloud.vrml.cache.api.CacheStats> doGetStats() {
        return Try.of(() -> {
            com.github.benmanes.caffeine.cache.stats.CacheStats caffeineStats = cache.stats();

            return group.rxcloud.vrml.cache.api.CacheStats.builder()
                    .hitCount(caffeineStats.hitCount())
                    .missCount(caffeineStats.missCount())
                    .loadCount(caffeineStats.loadCount())
                    .totalLoadTime(caffeineStats.totalLoadTime())
                    .evictionCount(caffeineStats.evictionCount())
                    .size(cache.estimatedSize())
                    .build();
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to get cache stats", throwable);
            return group.rxcloud.vrml.cache.api.CacheStats.builder().build();
        });
    }

    @Override
    public Try<Boolean> healthCheck() {
        return Try.of(() -> {
            // 执行简单的缓存操作检查健康状态
            String testKey = "__health_check__";
            String testValue = "test";

            cache.put(testKey, new CacheValue(testValue, Duration.ofSeconds(1)));
            CacheValue retrieved = cache.getIfPresent(testKey);
            cache.invalidate(testKey);

            return retrieved != null && testValue.equals(retrieved.getValue());
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Caffeine health check failed", throwable);
            return false;
        });
    }

    /**
     * 类型转换
     *
     * @param value     原始值
     * @param valueType 目标类型
     * @param <T>       目标类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> valueType) {
        if (value == null) {
            return null;
        }

        if (valueType.isInstance(value)) {
            return (T) value;
        }

        // 基本类型转换
        if (valueType == String.class) {
            return (T) String.valueOf(value);
        }

        if (valueType == Integer.class || valueType == int.class) {
            if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            }
            if (value instanceof String) {
                return (T) Integer.valueOf((String) value);
            }
        }

        if (valueType == Long.class || valueType == long.class) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
            if (value instanceof String) {
                return (T) Long.valueOf((String) value);
            }
        }

        if (valueType == Boolean.class || valueType == boolean.class) {
            if (value instanceof String) {
                return (T) Boolean.valueOf((String) value);
            }
        }

        // 尝试直接转换
        return valueType.cast(value);
    }

    /**
     * 清理过期的缓存项（定期调用）
     */
    public void cleanupExpired() {
        Try.run(() -> {
            long currentTime = System.currentTimeMillis();
            List<String> expiredKeys = expirationTimes.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0 && entry.getValue() < currentTime)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!expiredKeys.isEmpty()) {
                cache.invalidateAll(expiredKeys);
                expiredKeys.forEach(expirationTimes::remove);
                log(LogLevel.DEBUG, "Cleaned up {} expired cache entries", expiredKeys.size());
            }
        }).recover(throwable -> {
            log(LogLevel.WARN, "Failed to cleanup expired cache entries", throwable);
            return null;
        });
    }
}