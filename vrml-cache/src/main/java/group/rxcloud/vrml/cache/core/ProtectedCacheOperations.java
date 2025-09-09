package group.rxcloud.vrml.cache.core;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.cache.monitoring.CacheMonitor;
import group.rxcloud.vrml.cache.protection.CacheProtection;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 带防护和监控的缓存操作包装器
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class ProtectedCacheOperations extends AbstractCacheOperations {
    
    private final CacheOperations delegate;
    private final CacheProtection protection;
    private final CacheMonitor monitor;
    private final String cacheName;
    
    /**
     * 构造函数
     * 
     * @param delegate 委托的缓存操作
     * @param protection 缓存防护
     * @param monitor 缓存监控
     * @param cacheName 缓存名称
     */
    public ProtectedCacheOperations(CacheOperations delegate, CacheProtection protection, 
                                   CacheMonitor monitor, String cacheName) {
        super(delegate instanceof AbstractCacheOperations 
            ? ((AbstractCacheOperations) delegate).pattern 
            : "protected", 
            delegate instanceof AbstractCacheOperations 
            ? ((AbstractCacheOperations) delegate).config 
            : null);
        this.delegate = delegate;
        this.protection = protection;
        this.monitor = monitor;
        this.cacheName = cacheName;
        
        // 注册到监控系统
        if (monitor != null) {
            monitor.registerCache(cacheName, this);
        }
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 其他实例
     */
    private ProtectedCacheOperations(ProtectedCacheOperations other) {
        super(other);
        this.delegate = other.delegate;
        this.protection = other.protection;
        this.monitor = other.monitor;
        this.cacheName = other.cacheName;
    }
    
    @Override
    protected AbstractCacheOperations createCopy() {
        return new ProtectedCacheOperations(this);
    }
    
    @Override
    public <T> Try<Option<T>> get(String key, Class<T> valueType) {
        long startTime = System.currentTimeMillis();
        
        Try<Option<T>> result = executeWithIntegration("get", () -> {
            return delegate.get(key, valueType);
        });
        
        // 记录监控指标
        recordMetrics("get", startTime, result.isSuccess());
        
        return result;
    }
    
    @Override
    public <T> Try<T> getOrLoad(String key, Class<T> valueType, Supplier<T> loader) {
        return getOrLoad(key, valueType, loader, config != null ? config.getDefaultTtl() : Duration.ofHours(1));
    }
    
    @Override
    public <T> Try<T> getOrLoad(String key, Class<T> valueType, Supplier<T> loader, Duration ttl) {
        long startTime = System.currentTimeMillis();
        
        Try<T> result = executeWithIntegration("getOrLoad", () -> {
            if (protection != null) {
                // 使用防护机制
                return protection.protectedGetOrLoad(delegate, key, valueType, loader, ttl);
            } else {
                // 直接使用委托的getOrLoad
                return delegate.getOrLoad(key, valueType, loader, ttl);
            }
        });
        
        // 记录监控指标
        recordMetrics("getOrLoad", startTime, result.isSuccess());
        
        // 如果成功加载，记录key存在
        if (result.isSuccess() && result.get() != null && protection != null) {
            protection.recordKeyExists(key);
        }
        
        return result;
    }
    
    @Override
    public Try<Void> put(String key, Object value, Duration ttl) {
        long startTime = System.currentTimeMillis();
        
        Try<Void> result = executeWithIntegration("put", () -> {
            Duration protectedTtl = ttl;
            if (protection != null) {
                protectedTtl = protection.applyAvalancheProtection(ttl);
            }
            
            Try<Void> putResult = delegate.put(key, value, protectedTtl);
            
            // 记录key存在
            if (putResult.isSuccess() && protection != null) {
                protection.recordKeyExists(key);
            }
            
            return putResult;
        });
        
        // 记录监控指标
        recordMetrics("put", startTime, result.isSuccess());
        
        return result;
    }
    
    @Override
    public Try<Boolean> evict(String key) {
        long startTime = System.currentTimeMillis();
        
        Try<Boolean> result = executeWithIntegration("evict", () -> {
            return delegate.evict(key);
        });
        
        recordMetrics("evict", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Long> evictAll(List<String> keys) {
        long startTime = System.currentTimeMillis();
        
        Try<Long> result = executeWithIntegration("evictAll", () -> {
            return delegate.evictAll(keys);
        });
        
        recordMetrics("evictAll", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Long> evictByPattern(String pattern) {
        long startTime = System.currentTimeMillis();
        
        Try<Long> result = executeWithIntegration("evictByPattern", () -> {
            return delegate.evictByPattern(pattern);
        });
        
        recordMetrics("evictByPattern", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Boolean> exists(String key) {
        long startTime = System.currentTimeMillis();
        
        Try<Boolean> result = executeWithIntegration("exists", () -> {
            return delegate.exists(key);
        });
        
        recordMetrics("exists", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Boolean> expire(String key, Duration ttl) {
        long startTime = System.currentTimeMillis();
        
        Try<Boolean> result = executeWithIntegration("expire", () -> {
            Duration protectedTtl = ttl;
            if (protection != null) {
                protectedTtl = protection.applyAvalancheProtection(ttl);
            }
            return delegate.expire(key, protectedTtl);
        });
        
        recordMetrics("expire", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Duration> getTtl(String key) {
        long startTime = System.currentTimeMillis();
        
        Try<Duration> result = executeWithIntegration("getTtl", () -> {
            return delegate.getTtl(key);
        });
        
        recordMetrics("getTtl", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public <T> Try<Map<String, T>> multiGet(List<String> keys, Class<T> valueType) {
        long startTime = System.currentTimeMillis();
        
        Try<Map<String, T>> result = executeWithIntegration("multiGet", () -> {
            return delegate.multiGet(keys, valueType);
        });
        
        recordMetrics("multiGet", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Void> multiPut(Map<String, Object> keyValues, Duration ttl) {
        long startTime = System.currentTimeMillis();
        
        Try<Void> result = executeWithIntegration("multiPut", () -> {
            Duration protectedTtl = ttl;
            if (protection != null) {
                protectedTtl = protection.applyAvalancheProtection(ttl);
            }
            
            Try<Void> putResult = delegate.multiPut(keyValues, protectedTtl);
            
            // 记录所有key存在
            if (putResult.isSuccess() && protection != null) {
                keyValues.keySet().forEach(protection::recordKeyExists);
            }
            
            return putResult;
        });
        
        recordMetrics("multiPut", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Void> clear() {
        long startTime = System.currentTimeMillis();
        
        Try<Void> result = executeWithIntegration("clear", () -> {
            return delegate.clear();
        });
        
        recordMetrics("clear", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<CacheStats> getStats() {
        long startTime = System.currentTimeMillis();
        
        Try<CacheStats> result = executeWithIntegration("getStats", () -> {
            return delegate.getStats();
        });
        
        recordMetrics("getStats", startTime, result.isSuccess());
        return result;
    }
    
    @Override
    public Try<Boolean> healthCheck() {
        long startTime = System.currentTimeMillis();
        
        Try<Boolean> result = executeWithIntegration("healthCheck", () -> {
            return delegate.healthCheck();
        });
        
        recordMetrics("healthCheck", startTime, result.isSuccess());
        return result;
    }
    
    /**
     * 记录监控指标
     * 
     * @param operation 操作名称
     * @param startTime 开始时间
     * @param success 是否成功
     */
    private void recordMetrics(String operation, long startTime, boolean success) {
        if (monitor != null) {
            long responseTime = System.currentTimeMillis() - startTime;
            monitor.recordOperation(cacheName, operation, responseTime, success);
        }
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        if (protection != null) {
            protection.cleanup();
        }
        
        if (monitor != null) {
            monitor.unregisterCache(cacheName);
        }
    }
    
    // 委托方法实现（AbstractCacheOperations要求的抽象方法）
    
    @Override
    protected <T> Try<Option<T>> doGet(String key, Class<T> valueType) {
        return delegate.get(key, valueType);
    }
    
    @Override
    protected Try<Void> doPut(String key, Object value, Duration ttl) {
        return delegate.put(key, value, ttl);
    }
    
    @Override
    protected Try<Boolean> doEvict(String key) {
        return delegate.evict(key);
    }
    
    @Override
    protected Try<Long> doEvictAll(List<String> keys) {
        return delegate.evictAll(keys);
    }
    
    @Override
    protected Try<Long> doEvictByPattern(String pattern) {
        return delegate.evictByPattern(pattern);
    }
    
    @Override
    protected Try<Boolean> doExists(String key) {
        return delegate.exists(key);
    }
    
    @Override
    protected Try<Boolean> doExpire(String key, Duration ttl) {
        return delegate.expire(key, ttl);
    }
    
    @Override
    protected Try<Duration> doGetTtl(String key) {
        return delegate.getTtl(key);
    }
    
    @Override
    protected <T> Try<Map<String, T>> doMultiGet(List<String> keys, Class<T> valueType) {
        return delegate.multiGet(keys, valueType);
    }
    
    @Override
    protected Try<Void> doMultiPut(Map<String, Object> keyValues, Duration ttl) {
        return delegate.multiPut(keyValues, ttl);
    }
    
    @Override
    protected Try<Void> doClear() {
        return delegate.clear();
    }
    
    @Override
    protected Try<CacheStats> doGetStats() {
        return delegate.getStats();
    }
}