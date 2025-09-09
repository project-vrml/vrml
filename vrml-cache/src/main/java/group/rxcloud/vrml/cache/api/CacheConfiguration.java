package group.rxcloud.vrml.cache.api;

import group.rxcloud.vrml.core.api.VrmlConfiguration;

import java.time.Duration;

/**
 * 缓存配置接口
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface CacheConfiguration extends VrmlConfiguration {
    
    /**
     * 获取缓存类型
     * 
     * @return 缓存类型（redis, caffeine, multilevel等）
     */
    String getCacheType();
    
    /**
     * 获取默认TTL
     * 
     * @return 默认过期时间
     */
    default Duration getDefaultTtl() {
        return Duration.ofHours(1);
    }
    
    /**
     * 获取最大缓存大小
     * 
     * @return 最大缓存大小，-1表示无限制
     */
    default long getMaxSize() {
        return 10000L;
    }
    
    /**
     * 是否启用缓存穿透防护
     * 
     * @return true表示启用
     */
    default boolean isBloomFilterEnabled() {
        return false;
    }
    
    /**
     * 是否启用缓存击穿防护
     * 
     * @return true表示启用
     */
    default boolean isHotKeyProtectionEnabled() {
        return false;
    }
    
    /**
     * 是否启用缓存雪崩防护
     * 
     * @return true表示启用
     */
    default boolean isAvalancheProtectionEnabled() {
        return false;
    }
    
    /**
     * 获取缓存雪崩防护的随机TTL范围
     * 
     * @return 随机TTL范围
     */
    default Duration getRandomTtlRange() {
        return Duration.ofMinutes(5);
    }
    
    /**
     * 是否启用缓存预热
     * 
     * @return true表示启用
     */
    default boolean isWarmUpEnabled() {
        return false;
    }
    
    /**
     * 获取序列化器类型
     * 
     * @return 序列化器类型（json, jdk, kryo等）
     */
    default String getSerializerType() {
        return "json";
    }
    
    /**
     * 获取压缩类型
     * 
     * @return 压缩类型（none, gzip, lz4等）
     */
    default String getCompressionType() {
        return "none";
    }
    
    /**
     * 获取压缩阈值（字节）
     * 
     * @return 压缩阈值，超过此大小才压缩
     */
    default int getCompressionThreshold() {
        return 1024;
    }
    
    @Override
    default String getConfigPrefix() {
        return "vrml.cache";
    }
}