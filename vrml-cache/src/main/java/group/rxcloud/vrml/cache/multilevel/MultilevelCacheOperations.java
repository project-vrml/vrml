package group.rxcloud.vrml.cache.multilevel;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.cache.core.AbstractCacheOperations;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 多级缓存操作实现
 * 支持L1(本地缓存)和L2(远程缓存)的协调
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class MultilevelCacheOperations extends AbstractCacheOperations {
    
    private final CacheOperations l1Cache; // 本地缓存（L1）
    private final CacheOperations l2Cache; // 远程缓存（L2）
    private final MultilevelCacheConfiguration multilevelConfig;
    private final ExecutorService asyncExecutor;
    
    /**
     * 构造函数
     * 
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @param l1Cache L1缓存（本地）
     * @param l2Cache L2缓存（远程）
     */
    public MultilevelCacheOperations(String pattern, CacheConfiguration config,
                                   CacheOperations l1Cache, CacheOperations l2Cache) {
        super(pattern, config);
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
        this.multilevelConfig = config instanceof MultilevelCacheConfiguration 
            ? (MultilevelCacheConfiguration) config 
            : new MultilevelCacheConfiguration();
        this.asyncExecutor = Executors.newFixedThreadPool(
            multilevelConfig.getAsyncThreadPoolSize(),
            r -> {
                Thread t = new Thread(r, "vrml-cache-multilevel-" + System.currentTimeMillis());
                t.setDaemon(true);
                return t;
            }
        );
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 其他实例
     */
    private MultilevelCacheOperations(MultilevelCacheOperations other) {
        super(other);
        this.l1Cache = other.l1Cache;
        this.l2Cache = other.l2Cache;
        this.multilevelConfig = other.multilevelConfig;
        this.asyncExecutor = other.asyncExecutor; // 共享线程池
    }
    
    @Override
    protected AbstractCacheOperations createCopy() {
        return new MultilevelCacheOperations(this);
    }
    
    @Override
    protected <T> Try<Option<T>> doGet(String key, Class<T> valueType) {
        return Try.of(() -> {
            // 1. 先从L1缓存获取
            Try<Option<T>> l1Result = l1Cache.get(key, valueType);
            if (l1Result.isSuccess() && l1Result.get().isDefined()) {
                recordCount("l1.hit", true);
                log(LogLevel.DEBUG, "L1 cache hit for key: {}", key);
                return l1Result.get();
            }
            
            recordCount("l1.miss", true);
            
            // 2. L1未命中，从L2缓存获取
            Try<Option<T>> l2Result = l2Cache.get(key, valueType);
            if (l2Result.isFailure()) {
                recordCount("l2.error", true);
                log(LogLevel.WARN, "L2 cache error for key: {}", key, l2Result.getCause());
                return Option.none();
            }
            
            if (l2Result.get().isDefined()) {
                recordCount("l2.hit", true);
                log(LogLevel.DEBUG, "L2 cache hit for key: {}", key);
                
                // 3. L2命中，异步回写到L1
                T value = l2Result.get().get();
                if (multilevelConfig.isL1WriteBackEnabled()) {
                    asyncWriteBackToL1(key, value);
                }
                
                return Option.of(value);
            }
            
            recordCount("l2.miss", true);
            log(LogLevel.DEBUG, "Both L1 and L2 cache miss for key: {}", key);
            return Option.none();
        });
    }
    
    @Override
    protected Try<Void> doPut(String key, Object value, Duration ttl) {
        return Try.run(() -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            // 1. 写入L1缓存
            if (multilevelConfig.isL1WriteEnabled()) {
                CompletableFuture<Void> l1Future = CompletableFuture.runAsync(() -> {
                    Try<Void> l1Result = l1Cache.put(key, value, ttl);
                    if (l1Result.isFailure()) {
                        recordCount("l1.write.error", true);
                        log(LogLevel.WARN, "L1 cache write failed for key: {}", key, l1Result.getCause());
                    } else {
                        recordCount("l1.write.success", true);
                    }
                }, asyncExecutor);
                futures.add(l1Future);
            }
            
            // 2. 写入L2缓存
            if (multilevelConfig.isL2WriteEnabled()) {
                CompletableFuture<Void> l2Future = CompletableFuture.runAsync(() -> {
                    Try<Void> l2Result = l2Cache.put(key, value, ttl);
                    if (l2Result.isFailure()) {
                        recordCount("l2.write.error", true);
                        log(LogLevel.WARN, "L2 cache write failed for key: {}", key, l2Result.getCause());
                    } else {
                        recordCount("l2.write.success", true);
                    }
                }, asyncExecutor);
                futures.add(l2Future);
            }
            
            // 3. 根据策略等待完成
            if (multilevelConfig.getWriteStrategy() == MultilevelCacheConfiguration.WriteStrategy.WRITE_THROUGH) {
                // 写穿策略：等待所有写入完成
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            } else if (multilevelConfig.getWriteStrategy() == MultilevelCacheConfiguration.WriteStrategy.WRITE_BEHIND) {
                // 写回策略：异步写入，不等待
                // futures会在后台执行
            }
        });
    }
    
    @Override
    protected Try<Boolean> doEvict(String key) {
        return Try.of(() -> {
            boolean l1Evicted = false;
            boolean l2Evicted = false;
            
            // 1. 从L1缓存删除
            Try<Boolean> l1Result = l1Cache.evict(key);
            if (l1Result.isSuccess()) {
                l1Evicted = l1Result.get();
                recordCount("l1.evict.success", true);
            } else {
                recordCount("l1.evict.error", true);
                log(LogLevel.WARN, "L1 cache evict failed for key: {}", key, l1Result.getCause());
            }
            
            // 2. 从L2缓存删除
            Try<Boolean> l2Result = l2Cache.evict(key);
            if (l2Result.isSuccess()) {
                l2Evicted = l2Result.get();
                recordCount("l2.evict.success", true);
            } else {
                recordCount("l2.evict.error", true);
                log(LogLevel.WARN, "L2 cache evict failed for key: {}", key, l2Result.getCause());
            }
            
            return l1Evicted || l2Evicted;
        });
    }
    
    @Override
    protected Try<Long> doEvictAll(List<String> keys) {
        return Try.of(() -> {
            long totalEvicted = 0;
            
            // 1. 从L1缓存批量删除
            Try<Long> l1Result = l1Cache.evictAll(keys);
            if (l1Result.isSuccess()) {
                totalEvicted += l1Result.get();
                recordCount("l1.evictAll.success", true);
            } else {
                recordCount("l1.evictAll.error", true);
                log(LogLevel.WARN, "L1 cache evictAll failed for keys: {}", keys, l1Result.getCause());
            }
            
            // 2. 从L2缓存批量删除
            Try<Long> l2Result = l2Cache.evictAll(keys);
            if (l2Result.isSuccess()) {
                // 不重复计算，因为可能同一个key在两个缓存中都存在
                recordCount("l2.evictAll.success", true);
            } else {
                recordCount("l2.evictAll.error", true);
                log(LogLevel.WARN, "L2 cache evictAll failed for keys: {}", keys, l2Result.getCause());
            }
            
            return totalEvicted;
        });
    }
    
    @Override
    protected Try<Long> doEvictByPattern(String keyPattern) {
        return Try.of(() -> {
            long totalEvicted = 0;
            
            // 1. 从L1缓存按模式删除
            Try<Long> l1Result = l1Cache.evictByPattern(keyPattern);
            if (l1Result.isSuccess()) {
                totalEvicted += l1Result.get();
                recordCount("l1.evictByPattern.success", true);
            } else {
                recordCount("l1.evictByPattern.error", true);
                log(LogLevel.WARN, "L1 cache evictByPattern failed for pattern: {}", keyPattern, l1Result.getCause());
            }
            
            // 2. 从L2缓存按模式删除
            Try<Long> l2Result = l2Cache.evictByPattern(keyPattern);
            if (l2Result.isSuccess()) {
                recordCount("l2.evictByPattern.success", true);
            } else {
                recordCount("l2.evictByPattern.error", true);
                log(LogLevel.WARN, "L2 cache evictByPattern failed for pattern: {}", keyPattern, l2Result.getCause());
            }
            
            return totalEvicted;
        });
    }
    
    @Override
    protected Try<Boolean> doExists(String key) {
        return Try.of(() -> {
            // 先检查L1缓存
            Try<Boolean> l1Result = l1Cache.exists(key);
            if (l1Result.isSuccess() && l1Result.get()) {
                return true;
            }
            
            // 再检查L2缓存
            Try<Boolean> l2Result = l2Cache.exists(key);
            return l2Result.isSuccess() && l2Result.get();
        });
    }
    
    @Override
    protected Try<Boolean> doExpire(String key, Duration ttl) {
        return Try.of(() -> {
            boolean l1Success = false;
            boolean l2Success = false;
            
            // 1. 设置L1缓存过期时间
            Try<Boolean> l1Result = l1Cache.expire(key, ttl);
            if (l1Result.isSuccess()) {
                l1Success = l1Result.get();
            } else {
                log(LogLevel.WARN, "L1 cache expire failed for key: {}", key, l1Result.getCause());
            }
            
            // 2. 设置L2缓存过期时间
            Try<Boolean> l2Result = l2Cache.expire(key, ttl);
            if (l2Result.isSuccess()) {
                l2Success = l2Result.get();
            } else {
                log(LogLevel.WARN, "L2 cache expire failed for key: {}", key, l2Result.getCause());
            }
            
            return l1Success || l2Success;
        });
    }
    
    @Override
    protected Try<Duration> doGetTtl(String key) {
        return Try.of(() -> {
            // 优先从L1获取TTL
            Try<Duration> l1Result = l1Cache.getTtl(key);
            if (l1Result.isSuccess() && l1Result.get().getSeconds() >= 0) {
                return l1Result.get();
            }
            
            // L1没有或失败，从L2获取
            Try<Duration> l2Result = l2Cache.getTtl(key);
            if (l2Result.isSuccess()) {
                return l2Result.get();
            }
            
            return Duration.ofSeconds(-2); // 键不存在
        });
    }
    
    @Override
    protected <T> Try<Map<String, T>> doMultiGet(List<String> keys, Class<T> valueType) {
        return Try.of(() -> {
            Map<String, T> result = new HashMap<>();
            List<String> l2Keys = new ArrayList<>();
            
            // 1. 先从L1批量获取
            Try<Map<String, T>> l1Result = l1Cache.multiGet(keys, valueType);
            if (l1Result.isSuccess()) {
                result.putAll(l1Result.get());
                recordCount("l1.multiGet.success", true);
                
                // 找出L1未命中的键
                l2Keys = keys.stream()
                    .filter(key -> !result.containsKey(formatKey(key)))
                    .collect(Collectors.toList());
            } else {
                recordCount("l1.multiGet.error", true);
                l2Keys = keys;
            }
            
            // 2. 从L2获取L1未命中的键
            if (!l2Keys.isEmpty()) {
                Try<Map<String, T>> l2Result = l2Cache.multiGet(l2Keys, valueType);
                if (l2Result.isSuccess()) {
                    Map<String, T> l2Values = l2Result.get();
                    result.putAll(l2Values);
                    recordCount("l2.multiGet.success", true);
                    
                    // 3. 异步回写L2命中的数据到L1
                    if (multilevelConfig.isL1WriteBackEnabled() && !l2Values.isEmpty()) {
                        asyncWriteBackToL1(l2Values);
                    }
                } else {
                    recordCount("l2.multiGet.error", true);
                    log(LogLevel.WARN, "L2 cache multiGet failed for keys: {}", l2Keys, l2Result.getCause());
                }
            }
            
            return result;
        });
    }
    
    @Override
    protected Try<Void> doMultiPut(Map<String, Object> keyValues, Duration ttl) {
        return Try.run(() -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            // 1. 写入L1缓存
            if (multilevelConfig.isL1WriteEnabled()) {
                CompletableFuture<Void> l1Future = CompletableFuture.runAsync(() -> {
                    Try<Void> l1Result = l1Cache.multiPut(keyValues, ttl);
                    if (l1Result.isFailure()) {
                        recordCount("l1.multiPut.error", true);
                        log(LogLevel.WARN, "L1 cache multiPut failed", l1Result.getCause());
                    } else {
                        recordCount("l1.multiPut.success", true);
                    }
                }, asyncExecutor);
                futures.add(l1Future);
            }
            
            // 2. 写入L2缓存
            if (multilevelConfig.isL2WriteEnabled()) {
                CompletableFuture<Void> l2Future = CompletableFuture.runAsync(() -> {
                    Try<Void> l2Result = l2Cache.multiPut(keyValues, ttl);
                    if (l2Result.isFailure()) {
                        recordCount("l2.multiPut.error", true);
                        log(LogLevel.WARN, "L2 cache multiPut failed", l2Result.getCause());
                    } else {
                        recordCount("l2.multiPut.success", true);
                    }
                }, asyncExecutor);
                futures.add(l2Future);
            }
            
            // 3. 根据策略等待完成
            if (multilevelConfig.getWriteStrategy() == MultilevelCacheConfiguration.WriteStrategy.WRITE_THROUGH) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
        });
    }
    
    @Override
    protected Try<Void> doClear() {
        return Try.run(() -> {
            // 清空两级缓存
            Try<Void> l1Result = l1Cache.clear();
            if (l1Result.isFailure()) {
                log(LogLevel.WARN, "L1 cache clear failed", l1Result.getCause());
            }
            
            Try<Void> l2Result = l2Cache.clear();
            if (l2Result.isFailure()) {
                log(LogLevel.WARN, "L2 cache clear failed", l2Result.getCause());
            }
        });
    }
    
    @Override
    protected Try<CacheStats> doGetStats() {
        return Try.of(() -> {
            // 合并两级缓存的统计信息
            CacheStats l1Stats = l1Cache.getStats().getOrElse(CacheStats.builder().build());
            CacheStats l2Stats = l2Cache.getStats().getOrElse(CacheStats.builder().build());
            
            return CacheStats.builder()
                .hitCount(l1Stats.getHitCount() + l2Stats.getHitCount())
                .missCount(l1Stats.getMissCount() + l2Stats.getMissCount())
                .loadCount(l1Stats.getLoadCount() + l2Stats.getLoadCount())
                .loadExceptionCount(l1Stats.getLoadExceptionCount() + l2Stats.getLoadExceptionCount())
                .totalLoadTime(l1Stats.getTotalLoadTime() + l2Stats.getTotalLoadTime())
                .evictionCount(l1Stats.getEvictionCount() + l2Stats.getEvictionCount())
                .size(l1Stats.getSize() + l2Stats.getSize())
                .build();
        });
    }
    
    @Override
    public Try<Boolean> healthCheck() {
        return Try.of(() -> {
            // 检查两级缓存的健康状态
            Try<Boolean> l1Health = l1Cache.healthCheck();
            Try<Boolean> l2Health = l2Cache.healthCheck();
            
            boolean l1Healthy = l1Health.isSuccess() && l1Health.get();
            boolean l2Healthy = l2Health.isSuccess() && l2Health.get();
            
            // 根据配置决定健康检查策略
            if (multilevelConfig.isStrictHealthCheck()) {
                return l1Healthy && l2Healthy; // 严格模式：两级都健康才算健康
            } else {
                return l1Healthy || l2Healthy; // 宽松模式：任一级健康就算健康
            }
        });
    }
    
    /**
     * 异步回写单个值到L1缓存
     * 
     * @param key 缓存键
     * @param value 缓存值
     */
    private void asyncWriteBackToL1(String key, Object value) {
        CompletableFuture.runAsync(() -> {
            Try<Void> result = l1Cache.put(key, value);
            if (result.isFailure()) {
                recordCount("l1.writeBack.error", true);
                log(LogLevel.DEBUG, "L1 write-back failed for key: {}", key, result.getCause());
            } else {
                recordCount("l1.writeBack.success", true);
                log(LogLevel.DEBUG, "L1 write-back success for key: {}", key);
            }
        }, asyncExecutor);
    }
    
    /**
     * 异步回写多个值到L1缓存
     * 
     * @param keyValues 键值对映射
     */
    private <T> void asyncWriteBackToL1(Map<String, T> keyValues) {
        CompletableFuture.runAsync(() -> {
            Map<String, Object> objectMap = new HashMap<>(keyValues);
            Try<Void> result = l1Cache.multiPut(objectMap);
            if (result.isFailure()) {
                recordCount("l1.multiWriteBack.error", true);
                log(LogLevel.DEBUG, "L1 multi write-back failed", result.getCause());
            } else {
                recordCount("l1.multiWriteBack.success", true);
                log(LogLevel.DEBUG, "L1 multi write-back success for {} keys", keyValues.size());
            }
        }, asyncExecutor);
    }
    
    /**
     * 关闭资源
     */
    public void shutdown() {
        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
            asyncExecutor.shutdown();
            try {
                // 等待5秒让任务完成
                if (!asyncExecutor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    log(LogLevel.WARN, "Async executor did not terminate gracefully, forcing shutdown");
                    asyncExecutor.shutdownNow();
                    // 再等待2秒
                    if (!asyncExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                        log(LogLevel.ERROR, "Async executor did not terminate after forced shutdown");
                    }
                }
            } catch (InterruptedException e) {
                log(LogLevel.WARN, "Interrupted while waiting for executor shutdown", e);
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}