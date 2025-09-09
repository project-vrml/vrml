package group.rxcloud.vrml.cache.config;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

/**
 * 默认缓存配置实现
 * 
 * @author VRML Team
 * @since 1.2.0
 */
@Data
public class DefaultCacheConfiguration implements CacheConfiguration {
    
    /**
     * 缓存类型
     */
    private String cacheType = "caffeine";
    
    /**
     * 默认TTL
     */
    private Duration defaultTtl = Duration.ofHours(1);
    
    /**
     * 最大缓存大小
     */
    private long maxSize = 10000L;
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 是否启用监控
     */
    private boolean metricsEnabled = true;
    
    /**
     * 是否启用链路追踪
     */
    private boolean traceEnabled = true;
    
    /**
     * 是否启用布隆过滤器
     */
    private boolean bloomFilterEnabled = false;
    
    /**
     * 是否启用热点key防护
     */
    private boolean hotKeyProtectionEnabled = false;
    
    /**
     * 是否启用雪崩防护
     */
    private boolean avalancheProtectionEnabled = false;
    
    /**
     * 随机TTL范围
     */
    private Duration randomTtlRange = Duration.ofMinutes(5);
    
    /**
     * 是否启用预热
     */
    private boolean warmUpEnabled = false;
    
    /**
     * 序列化器类型
     */
    private String serializerType = "json";
    
    /**
     * 压缩类型
     */
    private String compressionType = "none";
    
    /**
     * 压缩阈值
     */
    private int compressionThreshold = 1024;
    
    /**
     * 无参构造函数
     */
    public DefaultCacheConfiguration() {
        this.cacheType = "caffeine";
        this.defaultTtl = Duration.ofHours(1);
        this.maxSize = 10000L;
        this.enabled = true;
        this.metricsEnabled = true;
        this.traceEnabled = true;
        this.bloomFilterEnabled = false;
        this.hotKeyProtectionEnabled = false;
        this.avalancheProtectionEnabled = false;
        this.randomTtlRange = Duration.ofMinutes(5);
        this.warmUpEnabled = false;
        this.serializerType = "json";
        this.compressionType = "none";
        this.compressionThreshold = 1024;
    }
    
    /**
     * 创建Builder
     * 
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private final DefaultCacheConfiguration config = new DefaultCacheConfiguration();
        
        public Builder cacheType(String cacheType) {
            config.cacheType = cacheType;
            return this;
        }
        
        public Builder defaultTtl(Duration defaultTtl) {
            config.defaultTtl = defaultTtl;
            return this;
        }
        
        public Builder maxSize(long maxSize) {
            config.maxSize = maxSize;
            return this;
        }
        
        public Builder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }
        
        public Builder metricsEnabled(boolean metricsEnabled) {
            config.metricsEnabled = metricsEnabled;
            return this;
        }
        
        public Builder traceEnabled(boolean traceEnabled) {
            config.traceEnabled = traceEnabled;
            return this;
        }
        
        public Builder bloomFilterEnabled(boolean bloomFilterEnabled) {
            config.bloomFilterEnabled = bloomFilterEnabled;
            return this;
        }
        
        public Builder hotKeyProtectionEnabled(boolean hotKeyProtectionEnabled) {
            config.hotKeyProtectionEnabled = hotKeyProtectionEnabled;
            return this;
        }
        
        public Builder avalancheProtectionEnabled(boolean avalancheProtectionEnabled) {
            config.avalancheProtectionEnabled = avalancheProtectionEnabled;
            return this;
        }
        
        public Builder randomTtlRange(Duration randomTtlRange) {
            config.randomTtlRange = randomTtlRange;
            return this;
        }
        
        public Builder warmUpEnabled(boolean warmUpEnabled) {
            config.warmUpEnabled = warmUpEnabled;
            return this;
        }
        
        public Builder serializerType(String serializerType) {
            config.serializerType = serializerType;
            return this;
        }
        
        public Builder compressionType(String compressionType) {
            config.compressionType = compressionType;
            return this;
        }
        
        public Builder compressionThreshold(int compressionThreshold) {
            config.compressionThreshold = compressionThreshold;
            return this;
        }
        
        public DefaultCacheConfiguration build() {
            return config;
        }
    }
}