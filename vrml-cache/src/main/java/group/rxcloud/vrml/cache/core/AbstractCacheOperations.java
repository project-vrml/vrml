package group.rxcloud.vrml.cache.core;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.core.api.AbstractVrmlOperations;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.Tuple;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * 缓存操作抽象基类
 * 提供通用的缓存操作实现和防护机制
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public abstract class AbstractCacheOperations extends AbstractVrmlOperations implements CacheOperations {
    
    protected final String pattern;
    protected final CacheConfiguration config;
    
    /**
     * 构造函数
     * 
     * @param pattern 缓存键模式
     * @param config 缓存配置
     */
    protected AbstractCacheOperations(String pattern, CacheConfiguration config) {
        this.pattern = pattern;
        this.config = config;
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 其他实例
     */
    protected AbstractCacheOperations(AbstractCacheOperations other) {
        super(other);
        this.pattern = other.pattern;
        this.config = other.config;
    }
    
    @Override
    protected abstract AbstractCacheOperations createCopy();
    
    @Override
    public CacheOperations withMetrics(String metricName) {
        AbstractCacheOperations copy = createCopy();
        copy.metricName = metricName;
        return copy;
    }
    
    @Override
    public CacheOperations withTrace(String traceKey) {
        AbstractCacheOperations copy = createCopy();
        copy.traceKey = traceKey;
        return copy;
    }
    
    @Override
    public CacheOperations onError(java.util.function.Consumer<Throwable> errorHandler) {
        AbstractCacheOperations copy = createCopy();
        copy.errorHandler = errorHandler;
        return copy;
    }
    
    @Override
    public <T> Try<Option<T>> get(String key, Class<T> valueType) {
        return executeWithIntegration("get", () -> {
            String formattedKey = formatKey(key);
            return doGet(formattedKey, valueType);
        });
    }
    
    @Override
    public Try<Void> put(String key, Object value) {
        return put(key, value, config.getDefaultTtl());
    }
    
    @Override
    public Try<Void> put(String key, Object value, Duration ttl) {
        return executeWithIntegration("put", () -> {
            String formattedKey = formatKey(key);
            Duration actualTtl = applyAvalancheProtection(ttl);
            return doPut(formattedKey, value, actualTtl);
        });
    }
    
    @Override
    public <T> Try<T> getOrLoad(String key, Class<T> valueType, Supplier<T> loader) {
        return getOrLoad(key, valueType, loader, config.getDefaultTtl());
    }
    
    @Override
    public <T> Try<T> getOrLoad(String key, Class<T> valueType, Supplier<T> loader, Duration ttl) {
        return executeWithIntegration("getOrLoad", () -> {
            String formattedKey = formatKey(key);
            
            // 先尝试从缓存获取
            Try<Option<T>> cached = doGet(formattedKey, valueType);
            if (cached.isFailure()) {
                return Try.failure(cached.getCause());
            }
            
            if (cached.get().isDefined()) {
                recordCount("hit", true);
                return Try.success(cached.get().get());
            }
            
            // 缓存未命中，加载数据
            recordCount("miss", true);
            
            // 热点key防护
            if (config.isHotKeyProtectionEnabled()) {
                return loadWithHotKeyProtection(formattedKey, valueType, loader, ttl);
            } else {
                return loadAndCache(formattedKey, valueType, loader, ttl);
            }
        });
    }
    
    @Override
    public Try<Boolean> evict(String key) {
        return executeWithIntegration("evict", () -> {
            String formattedKey = formatKey(key);
            return doEvict(formattedKey);
        });
    }
    
    @Override
    public Try<Long> evictAll(List<String> keys) {
        return executeWithIntegration("evictAll", () -> {
            List<String> formattedKeys = keys.stream()
                .map(this::formatKey)
                .collect(java.util.stream.Collectors.toList());
            return doEvictAll(formattedKeys);
        });
    }
    
    @Override
    public Try<Long> evictByPattern(String keyPattern) {
        return executeWithIntegration("evictByPattern", () -> {
            return doEvictByPattern(keyPattern);
        });
    }
    
    @Override
    public Try<Boolean> exists(String key) {
        return executeWithIntegration("exists", () -> {
            String formattedKey = formatKey(key);
            return doExists(formattedKey);
        });
    }
    
    @Override
    public Try<Boolean> expire(String key, Duration ttl) {
        return executeWithIntegration("expire", () -> {
            String formattedKey = formatKey(key);
            Duration actualTtl = applyAvalancheProtection(ttl);
            return doExpire(formattedKey, actualTtl);
        });
    }
    
    @Override
    public Try<Duration> getTtl(String key) {
        return executeWithIntegration("getTtl", () -> {
            String formattedKey = formatKey(key);
            return doGetTtl(formattedKey);
        });
    }
    
    @Override
    public <T> Try<Map<String, T>> multiGet(List<String> keys, Class<T> valueType) {
        return executeWithIntegration("multiGet", () -> {
            List<String> formattedKeys = keys.stream()
                .map(this::formatKey)
                .collect(java.util.stream.Collectors.toList());
            return doMultiGet(formattedKeys, valueType);
        });
    }
    
    @Override
    public Try<Void> multiPut(Map<String, Object> keyValues) {
        return multiPut(keyValues, config.getDefaultTtl());
    }
    
    @Override
    public Try<Void> multiPut(Map<String, Object> keyValues, Duration ttl) {
        return executeWithIntegration("multiPut", () -> {
            Map<String, Object> formattedKeyValues = keyValues.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    entry -> formatKey(entry.getKey()),
                    Map.Entry::getValue
                ));
            Duration actualTtl = applyAvalancheProtection(ttl);
            return doMultiPut(formattedKeyValues, actualTtl);
        });
    }
    
    @Override
    public Try<Void> clear() {
        return executeWithIntegration("clear", () -> doClear());
    }
    
    @Override
    public Try<CacheStats> getStats() {
        return executeWithIntegration("getStats", () -> doGetStats());
    }
    
    /**
     * 格式化缓存键
     * 
     * @param key 原始键
     * @return 格式化后的键
     */
    protected String formatKey(String key) {
        if (pattern.contains("{}")) {
            return pattern.replace("{}", key);
        }
        return pattern + ":" + key;
    }
    
    /**
     * 应用雪崩防护
     * 
     * @param ttl 原始TTL
     * @return 应用防护后的TTL
     */
    protected Duration applyAvalancheProtection(Duration ttl) {
        if (!config.isAvalancheProtectionEnabled()) {
            return ttl;
        }
        
        // 添加随机时间避免缓存雪崩
        long randomSeconds = ThreadLocalRandom.current().nextLong(
            0, config.getRandomTtlRange().getSeconds());
        return ttl.plusSeconds(randomSeconds);
    }
    
    /**
     * 热点key防护加载
     * 
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 加载器
     * @param ttl 过期时间
     * @param <T> 值类型
     * @return 加载结果
     */
    protected <T> Try<T> loadWithHotKeyProtection(String key, Class<T> valueType, 
                                                  Supplier<T> loader, Duration ttl) {
        // 简单的热点key防护实现，可以根据需要扩展
        synchronized (this) {
            // 再次检查缓存
            Try<Option<T>> cached = doGet(key, valueType);
            if (cached.isSuccess() && cached.get().isDefined()) {
                return Try.success(cached.get().get());
            }
            
            return loadAndCache(key, valueType, loader, ttl);
        }
    }
    
    /**
     * 加载数据并缓存
     * 
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 加载器
     * @param ttl 过期时间
     * @param <T> 值类型
     * @return 加载结果
     */
    protected <T> Try<T> loadAndCache(String key, Class<T> valueType, 
                                      Supplier<T> loader, Duration ttl) {
        try {
            T value = loader.get();
            if (value != null) {
                Duration actualTtl = applyAvalancheProtection(ttl);
                Try<Void> putResult = doPut(key, value, actualTtl);
                if (putResult.isFailure()) {
                    log(LogLevel.WARN, "Failed to cache loaded value for key: {}", key, putResult.getCause());
                }
            }
            return Try.success(value);
        } catch (Exception e) {
            return Try.failure(e);
        }
    }
    
    // 抽象方法，由具体实现类提供
    
    /**
     * 获取缓存值的具体实现
     * 
     * @param key 缓存键
     * @param valueType 值类型
     * @param <T> 值类型
     * @return 缓存值
     */
    protected abstract <T> Try<Option<T>> doGet(String key, Class<T> valueType);
    
    /**
     * 设置缓存值的具体实现
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 过期时间
     * @return 操作结果
     */
    protected abstract Try<Void> doPut(String key, Object value, Duration ttl);
    
    /**
     * 删除缓存的具体实现
     * 
     * @param key 缓存键
     * @return 是否删除成功
     */
    protected abstract Try<Boolean> doEvict(String key);
    
    /**
     * 批量删除缓存的具体实现
     * 
     * @param keys 缓存键列表
     * @return 删除的数量
     */
    protected abstract Try<Long> doEvictAll(List<String> keys);
    
    /**
     * 按模式删除缓存的具体实现
     * 
     * @param pattern 键模式
     * @return 删除的数量
     */
    protected abstract Try<Long> doEvictByPattern(String pattern);
    
    /**
     * 检查缓存是否存在的具体实现
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    protected abstract Try<Boolean> doExists(String key);
    
    /**
     * 设置过期时间的具体实现
     * 
     * @param key 缓存键
     * @param ttl 过期时间
     * @return 操作结果
     */
    protected abstract Try<Boolean> doExpire(String key, Duration ttl);
    
    /**
     * 获取剩余过期时间的具体实现
     * 
     * @param key 缓存键
     * @return 剩余过期时间
     */
    protected abstract Try<Duration> doGetTtl(String key);
    
    /**
     * 批量获取缓存的具体实现
     * 
     * @param keys 缓存键列表
     * @param valueType 值类型
     * @param <T> 值类型
     * @return 键值对映射
     */
    protected abstract <T> Try<Map<String, T>> doMultiGet(List<String> keys, Class<T> valueType);
    
    /**
     * 批量设置缓存的具体实现
     * 
     * @param keyValues 键值对映射
     * @param ttl 过期时间
     * @return 操作结果
     */
    protected abstract Try<Void> doMultiPut(Map<String, Object> keyValues, Duration ttl);
    
    /**
     * 清空缓存的具体实现
     * 
     * @return 操作结果
     */
    protected abstract Try<Void> doClear();
    
    /**
     * 获取缓存统计的具体实现
     * 
     * @return 缓存统计
     */
    protected abstract Try<CacheStats> doGetStats();
}