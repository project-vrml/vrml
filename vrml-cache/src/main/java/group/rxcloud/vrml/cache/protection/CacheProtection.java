package group.rxcloud.vrml.cache.protection;

import group.rxcloud.vrml.cache.api.CacheOperations;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * 缓存防护机制
 * 提供缓存穿透、击穿、雪崩防护
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CacheProtection {
    
    private static final Logger log = LoggerFactory.getLogger(CacheProtection.class);
    
    /**
     * 布隆过滤器（简化实现）
     */
    private final BloomFilter bloomFilter;
    
    /**
     * 热点key保护信号量映射
     */
    private final ConcurrentMap<String, Semaphore> hotKeyProtection = new ConcurrentHashMap<>();
    
    /**
     * 空值缓存映射
     */
    private final ConcurrentMap<String, Long> nullValueCache = new ConcurrentHashMap<>();
    
    /**
     * 访问计数器
     */
    private final ConcurrentMap<String, AtomicLong> accessCounters = new ConcurrentHashMap<>();
    
    /**
     * 配置
     */
    private final ProtectionConfig config;
    
    /**
     * 构造函数
     * 
     * @param config 防护配置
     */
    public CacheProtection(ProtectionConfig config) {
        this.config = config;
        this.bloomFilter = new BloomFilter(config.getBloomFilterSize(), config.getBloomFilterHashFunctions());
    }
    
    /**
     * 防护配置
     */
    public static class ProtectionConfig {
        private boolean penetrationProtectionEnabled = true;
        private boolean hotKeyProtectionEnabled = true;
        private boolean avalancheProtectionEnabled = true;
        private int bloomFilterSize = 1000000;
        private int bloomFilterHashFunctions = 3;
        private Duration nullValueTtl = Duration.ofMinutes(5);
        private int hotKeyThreshold = 100;
        private Duration hotKeyWindow = Duration.ofMinutes(1);
        private int maxConcurrentLoads = 10;
        private Duration randomTtlRange = Duration.ofMinutes(5);
        
        // Getters and setters
        public boolean isPenetrationProtectionEnabled() { return penetrationProtectionEnabled; }
        public void setPenetrationProtectionEnabled(boolean penetrationProtectionEnabled) { this.penetrationProtectionEnabled = penetrationProtectionEnabled; }
        
        public boolean isHotKeyProtectionEnabled() { return hotKeyProtectionEnabled; }
        public void setHotKeyProtectionEnabled(boolean hotKeyProtectionEnabled) { this.hotKeyProtectionEnabled = hotKeyProtectionEnabled; }
        
        public boolean isAvalancheProtectionEnabled() { return avalancheProtectionEnabled; }
        public void setAvalancheProtectionEnabled(boolean avalancheProtectionEnabled) { this.avalancheProtectionEnabled = avalancheProtectionEnabled; }
        
        public int getBloomFilterSize() { return bloomFilterSize; }
        public void setBloomFilterSize(int bloomFilterSize) { this.bloomFilterSize = bloomFilterSize; }
        
        public int getBloomFilterHashFunctions() { return bloomFilterHashFunctions; }
        public void setBloomFilterHashFunctions(int bloomFilterHashFunctions) { this.bloomFilterHashFunctions = bloomFilterHashFunctions; }
        
        public Duration getNullValueTtl() { return nullValueTtl; }
        public void setNullValueTtl(Duration nullValueTtl) { this.nullValueTtl = nullValueTtl; }
        
        public int getHotKeyThreshold() { return hotKeyThreshold; }
        public void setHotKeyThreshold(int hotKeyThreshold) { this.hotKeyThreshold = hotKeyThreshold; }
        
        public Duration getHotKeyWindow() { return hotKeyWindow; }
        public void setHotKeyWindow(Duration hotKeyWindow) { this.hotKeyWindow = hotKeyWindow; }
        
        public int getMaxConcurrentLoads() { return maxConcurrentLoads; }
        public void setMaxConcurrentLoads(int maxConcurrentLoads) { this.maxConcurrentLoads = maxConcurrentLoads; }
        
        public Duration getRandomTtlRange() { return randomTtlRange; }
        public void setRandomTtlRange(Duration randomTtlRange) { this.randomTtlRange = randomTtlRange; }
    }
    
    /**
     * 防护的获取操作
     * 
     * @param cache 缓存操作
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 数据加载器
     * @param ttl 过期时间
     * @param <T> 值类型
     * @return 缓存值
     */
    public <T> Try<T> protectedGetOrLoad(CacheOperations cache, String key, Class<T> valueType, 
                                        Supplier<T> loader, Duration ttl) {
        return Try.of(() -> {
            // 1. 缓存穿透防护
            if (config.isPenetrationProtectionEnabled() && isPenetrationAttack(key)) {
                log.warn("[VRML-Cache] Potential penetration attack detected for key: {}", key);
                return null;
            }
            
            // 2. 先尝试从缓存获取
            Try<Option<T>> cached = cache.get(key, valueType);
            if (cached.isSuccess() && cached.get().isDefined()) {
                recordAccess(key);
                return cached.get().get();
            }
            
            // 3. 检查空值缓存
            if (isNullValueCached(key)) {
                log.debug("[VRML-Cache] Null value cached for key: {}", key);
                return null;
            }
            
            // 4. 热点key防护
            if (config.isHotKeyProtectionEnabled() && isHotKey(key)) {
                return protectedLoad(cache, key, valueType, loader, ttl);
            } else {
                return loadAndCache(cache, key, valueType, loader, ttl);
            }
        });
    }
    
    /**
     * 应用雪崩防护
     * 
     * @param ttl 原始TTL
     * @return 应用防护后的TTL
     */
    public Duration applyAvalancheProtection(Duration ttl) {
        if (!config.isAvalancheProtectionEnabled() || ttl == null) {
            return ttl;
        }
        
        // 添加随机时间避免缓存雪崩
        long randomMillis = ThreadLocalRandom.current().nextLong(
            0, config.getRandomTtlRange().toMillis());
        return ttl.plusMillis(randomMillis);
    }
    
    /**
     * 记录键存在（用于布隆过滤器）
     * 
     * @param key 缓存键
     */
    public void recordKeyExists(String key) {
        if (config.isPenetrationProtectionEnabled()) {
            bloomFilter.add(key);
        }
    }
    
    /**
     * 检查是否为穿透攻击
     * 
     * @param key 缓存键
     * @return 是否为穿透攻击
     */
    private boolean isPenetrationAttack(String key) {
        // 使用布隆过滤器检查key是否可能存在
        return !bloomFilter.mightContain(key);
    }
    
    /**
     * 检查是否为热点key
     * 
     * @param key 缓存键
     * @return 是否为热点key
     */
    private boolean isHotKey(String key) {
        AtomicLong counter = accessCounters.computeIfAbsent(key, k -> new AtomicLong(0));
        long count = counter.incrementAndGet();
        
        // 简单的热点检测：在时间窗口内访问次数超过阈值
        return count > config.getHotKeyThreshold();
    }
    
    /**
     * 记录访问
     * 
     * @param key 缓存键
     */
    private void recordAccess(String key) {
        accessCounters.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * 检查空值是否已缓存
     * 
     * @param key 缓存键
     * @return 是否已缓存空值
     */
    private boolean isNullValueCached(String key) {
        Long cachedTime = nullValueCache.get(key);
        if (cachedTime == null) {
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() - cachedTime > config.getNullValueTtl().toMillis()) {
            nullValueCache.remove(key);
            return false;
        }
        
        return true;
    }
    
    /**
     * 缓存空值
     * 
     * @param key 缓存键
     */
    private void cacheNullValue(String key) {
        nullValueCache.put(key, System.currentTimeMillis());
    }
    
    /**
     * 热点key保护的加载
     * 
     * @param cache 缓存操作
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 数据加载器
     * @param ttl 过期时间
     * @param <T> 值类型
     * @return 加载结果
     */
    private <T> T protectedLoad(CacheOperations cache, String key, Class<T> valueType, 
                               Supplier<T> loader, Duration ttl) {
        Semaphore semaphore = hotKeyProtection.computeIfAbsent(key, 
            k -> new Semaphore(config.getMaxConcurrentLoads()));
        
        try {
            // 尝试获取信号量
            if (semaphore.tryAcquire()) {
                try {
                    // 再次检查缓存（双重检查）
                    Try<Option<T>> cached = cache.get(key, valueType);
                    if (cached.isSuccess() && cached.get().isDefined()) {
                        return cached.get().get();
                    }
                    
                    // 加载数据
                    return loadAndCache(cache, key, valueType, loader, ttl);
                } finally {
                    semaphore.release();
                }
            } else {
                // 无法获取信号量，等待一段时间后重试获取缓存
                try {
                    Thread.sleep(50); // 等待50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                Try<Option<T>> cached = cache.get(key, valueType);
                if (cached.isSuccess() && cached.get().isDefined()) {
                    return cached.get().get();
                }
                
                // 如果还是没有，返回null或抛出异常
                log.warn("[VRML-Cache] Hot key protection: failed to acquire semaphore for key: {}", key);
                return null;
            }
        } catch (Exception e) {
            log.error("[VRML-Cache] Hot key protection failed for key: " + key, e);
            return loadAndCache(cache, key, valueType, loader, ttl);
        }
    }
    
    /**
     * 加载数据并缓存
     * 
     * @param cache 缓存操作
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 数据加载器
     * @param ttl 过期时间
     * @param <T> 值类型
     * @return 加载结果
     */
    private <T> T loadAndCache(CacheOperations cache, String key, Class<T> valueType, 
                              Supplier<T> loader, Duration ttl) {
        try {
            T value = loader.get();
            
            if (value != null) {
                // 记录key存在
                recordKeyExists(key);
                
                // 应用雪崩防护
                Duration protectedTtl = applyAvalancheProtection(ttl);
                
                // 缓存数据
                Try<Void> putResult = cache.put(key, value, protectedTtl);
                if (putResult.isFailure()) {
                    log.warn("[VRML-Cache] Failed to cache loaded value for key: {}", key, putResult.getCause());
                }
            } else {
                // 缓存空值防止穿透
                if (config.isPenetrationProtectionEnabled()) {
                    cacheNullValue(key);
                }
            }
            
            return value;
        } catch (Exception e) {
            log.error("[VRML-Cache] Failed to load data for key: " + key, e);
            throw new RuntimeException("Failed to load data for key: " + key, e);
        }
    }
    
    /**
     * 清理过期的访问计数器和空值缓存
     */
    public void cleanup() {
        Try.run(() -> {
            long currentTime = System.currentTimeMillis();
            long windowMillis = config.getHotKeyWindow().toMillis();
            
            // 清理访问计数器（简单实现：定期重置）
            if (currentTime % (windowMillis * 2) == 0) {
                accessCounters.clear();
                log.debug("[VRML-Cache] Cleaned up access counters");
            }
            
            // 清理过期的空值缓存
            nullValueCache.entrySet().removeIf(entry -> 
                currentTime - entry.getValue() > config.getNullValueTtl().toMillis());
            
            // 清理不再使用的热点key信号量
            hotKeyProtection.entrySet().removeIf(entry -> 
                !accessCounters.containsKey(entry.getKey()));
            
        }).recover(throwable -> {
            log.warn("[VRML-Cache] Failed to cleanup protection resources", throwable);
            return null;
        });
    }
    
    /**
     * 简化的布隆过滤器实现
     */
    private static class BloomFilter {
        private final int size;
        private final int hashFunctions;
        private final boolean[] bits;
        
        public BloomFilter(int size, int hashFunctions) {
            this.size = size;
            this.hashFunctions = hashFunctions;
            this.bits = new boolean[size];
        }
        
        public void add(String item) {
            for (int i = 0; i < hashFunctions; i++) {
                int hash = hash(item, i);
                bits[Math.abs(hash % size)] = true;
            }
        }
        
        public boolean mightContain(String item) {
            for (int i = 0; i < hashFunctions; i++) {
                int hash = hash(item, i);
                if (!bits[Math.abs(hash % size)]) {
                    return false;
                }
            }
            return true;
        }
        
        private int hash(String item, int seed) {
            // 使用更好的哈希算法
            int hash = seed;
            for (int i = 0; i < item.length(); i++) {
                hash = hash * 31 + item.charAt(i);
            }
            // 使用位运算确保正数
            return hash & 0x7FFFFFFF;
        }
    }
}