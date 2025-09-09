package group.rxcloud.vrml.cache.caffeine;

import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;

/**
 * Caffeine缓存配置
 * 
 * @author VRML Team
 * @since 1.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CaffeineCacheConfiguration extends DefaultCacheConfiguration {
    
    /**
     * 初始容量
     */
    private int initialCapacity = 100;
    
    /**
     * 并发级别
     */
    private int concurrencyLevel = 4;
    
    /**
     * 写入后过期时间
     */
    private Duration expireAfterWrite;
    
    /**
     * 访问后过期时间
     */
    private Duration expireAfterAccess;
    
    /**
     * 刷新后过期时间
     */
    private Duration refreshAfterWrite;
    
    /**
     * 弱键引用
     */
    private boolean weakKeys = false;
    
    /**
     * 弱值引用
     */
    private boolean weakValues = false;
    
    /**
     * 软值引用
     */
    private boolean softValues = false;
    
    /**
     * 是否启用统计
     */
    private boolean recordStats = true;
    
    /**
     * 过期清理间隔
     */
    private Duration cleanupInterval = Duration.ofMinutes(5);
    
    /**
     * 构造函数
     */
    public CaffeineCacheConfiguration() {
        super();
        setCacheType("caffeine");
        this.initialCapacity = 100;
        this.concurrencyLevel = 4;
        this.weakKeys = false;
        this.weakValues = false;
        this.softValues = false;
        this.recordStats = true;
        this.cleanupInterval = Duration.ofMinutes(5);
    }
    
    /**
     * 创建Caffeine缓存配置Builder
     * 
     * @return Builder实例
     */
    public static Builder caffeineBuilder() {
        return new Builder();
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private final CaffeineCacheConfiguration config = new CaffeineCacheConfiguration();
        
        public Builder initialCapacity(int initialCapacity) {
            config.initialCapacity = initialCapacity;
            return this;
        }
        
        public Builder maxSize(long maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }
        
        public Builder expireAfterWrite(Duration expireAfterWrite) {
            config.expireAfterWrite = expireAfterWrite;
            return this;
        }
        
        public Builder expireAfterAccess(Duration expireAfterAccess) {
            config.expireAfterAccess = expireAfterAccess;
            return this;
        }
        
        public Builder recordStats(boolean recordStats) {
            config.recordStats = recordStats;
            return this;
        }
        
        public Builder defaultTtl(Duration defaultTtl) {
            config.setDefaultTtl(defaultTtl);
            return this;
        }
        
        public CaffeineCacheConfiguration build() {
            return config;
        }
    }
    
    @Override
    public String getConfigPrefix() {
        return "vrml.cache.caffeine";
    }
}