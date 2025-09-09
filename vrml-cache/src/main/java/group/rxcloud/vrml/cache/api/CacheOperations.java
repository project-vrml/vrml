package group.rxcloud.vrml.cache.api;

import group.rxcloud.vrml.core.api.VrmlOperations;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 缓存操作接口
 * 提供统一的缓存操作API，支持不同的缓存实现
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface CacheOperations extends VrmlOperations {
    
    /**
     * 获取缓存值
     * 
     * @param key 缓存键
     * @param valueType 值类型
     * @param <T> 值类型
     * @return 缓存值，包装在Try和Option中
     */
    <T> Try<Option<T>> get(String key, Class<T> valueType);
    
    /**
     * 设置缓存值
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @return 操作结果
     */
    Try<Void> put(String key, Object value);
    
    /**
     * 设置缓存值并指定TTL
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 过期时间
     * @return 操作结果
     */
    Try<Void> put(String key, Object value, Duration ttl);
    
    /**
     * 获取缓存值，如果不存在则加载
     * 
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 加载器
     * @param <T> 值类型
     * @return 缓存值
     */
    <T> Try<T> getOrLoad(String key, Class<T> valueType, Supplier<T> loader);
    
    /**
     * 获取缓存值，如果不存在则加载并设置TTL
     * 
     * @param key 缓存键
     * @param valueType 值类型
     * @param loader 加载器
     * @param ttl 过期时间
     * @param <T> 值类型
     * @return 缓存值
     */
    <T> Try<T> getOrLoad(String key, Class<T> valueType, Supplier<T> loader, Duration ttl);
    
    /**
     * 删除缓存
     * 
     * @param key 缓存键
     * @return 操作结果
     */
    Try<Boolean> evict(String key);
    
    /**
     * 批量删除缓存
     * 
     * @param keys 缓存键列表
     * @return 操作结果
     */
    Try<Long> evictAll(List<String> keys);
    
    /**
     * 按模式删除缓存
     * 
     * @param pattern 键模式（支持通配符）
     * @return 删除的键数量
     */
    Try<Long> evictByPattern(String pattern);
    
    /**
     * 检查缓存是否存在
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    Try<Boolean> exists(String key);
    
    /**
     * 设置缓存过期时间
     * 
     * @param key 缓存键
     * @param ttl 过期时间
     * @return 操作结果
     */
    Try<Boolean> expire(String key, Duration ttl);
    
    /**
     * 获取缓存剩余过期时间
     * 
     * @param key 缓存键
     * @return 剩余过期时间，-1表示永不过期，-2表示键不存在
     */
    Try<Duration> getTtl(String key);
    
    /**
     * 批量获取缓存
     * 
     * @param keys 缓存键列表
     * @param valueType 值类型
     * @param <T> 值类型
     * @return 键值对映射
     */
    <T> Try<Map<String, T>> multiGet(List<String> keys, Class<T> valueType);
    
    /**
     * 批量设置缓存
     * 
     * @param keyValues 键值对映射
     * @return 操作结果
     */
    Try<Void> multiPut(Map<String, Object> keyValues);
    
    /**
     * 批量设置缓存并指定TTL
     * 
     * @param keyValues 键值对映射
     * @param ttl 过期时间
     * @return 操作结果
     */
    Try<Void> multiPut(Map<String, Object> keyValues, Duration ttl);
    
    /**
     * 清空所有缓存
     * 
     * @return 操作结果
     */
    Try<Void> clear();
    
    /**
     * 获取缓存统计信息
     * 
     * @return 缓存统计
     */
    Try<CacheStats> getStats();
    
    /**
     * 创建带监控的缓存操作
     * 
     * @param metricName 指标名称
     * @return 带监控的缓存操作
     */
    @Override
    CacheOperations withMetrics(String metricName);
    
    /**
     * 创建带链路追踪的缓存操作
     * 
     * @param traceKey 追踪键
     * @return 带追踪的缓存操作
     */
    @Override
    CacheOperations withTrace(String traceKey);
    
    /**
     * 创建带错误处理的缓存操作
     * 
     * @param errorHandler 错误处理器
     * @return 带错误处理的缓存操作
     */
    @Override
    CacheOperations onError(java.util.function.Consumer<Throwable> errorHandler);
}