package group.rxcloud.vrml.cache.multilevel;

import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;

/**
 * 多级缓存配置
 * 
 * @author VRML Team
 * @since 1.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MultilevelCacheConfiguration extends DefaultCacheConfiguration {
    
    /**
     * 写入策略枚举
     */
    public enum WriteStrategy {
        /**
         * 写穿策略：同步写入所有级别的缓存
         */
        WRITE_THROUGH,
        
        /**
         * 写回策略：异步写入，不等待完成
         */
        WRITE_BEHIND
    }
    
    /**
     * 是否启用L1缓存写入
     */
    private boolean l1WriteEnabled = true;
    
    /**
     * 是否启用L2缓存写入
     */
    private boolean l2WriteEnabled = true;
    
    /**
     * 是否启用L1缓存回写
     * 当L2命中时，是否将数据回写到L1
     */
    private boolean l1WriteBackEnabled = true;
    
    /**
     * 写入策略
     */
    private WriteStrategy writeStrategy = WriteStrategy.WRITE_THROUGH;
    
    /**
     * 异步线程池大小
     */
    private int asyncThreadPoolSize = 4;
    
    /**
     * L1缓存配置
     */
    private DefaultCacheConfiguration l1Config;
    
    /**
     * L2缓存配置
     */
    private DefaultCacheConfiguration l2Config;
    
    /**
     * 是否启用严格健康检查
     * true: 两级缓存都健康才算健康
     * false: 任一级缓存健康就算健康
     */
    private boolean strictHealthCheck = false;
    
    /**
     * L1缓存类型
     */
    private String l1CacheType = "caffeine";
    
    /**
     * L2缓存类型
     */
    private String l2CacheType = "redis";
    
    /**
     * L1缓存最大大小
     */
    private long l1MaxSize = 10000L;
    
    /**
     * L2缓存最大大小
     */
    private long l2MaxSize = 100000L;
    
    /**
     * L1缓存默认TTL
     */
    private Duration l1DefaultTtl = Duration.ofMinutes(30);
    
    /**
     * L2缓存默认TTL
     */
    private Duration l2DefaultTtl = Duration.ofHours(2);
    
    /**
     * 缓存一致性检查间隔
     */
    private Duration consistencyCheckInterval = Duration.ofMinutes(10);
    
    /**
     * 是否启用缓存一致性检查
     */
    private boolean consistencyCheckEnabled = false;
    
    /**
     * 构造函数
     */
    public MultilevelCacheConfiguration() {
        super();
        setCacheType("multilevel");
        this.l1WriteEnabled = true;
        this.l2WriteEnabled = true;
        this.l1WriteBackEnabled = true;
        this.writeStrategy = WriteStrategy.WRITE_THROUGH;
        this.asyncThreadPoolSize = 4;
        this.strictHealthCheck = false;
        this.l1CacheType = "caffeine";
        this.l2CacheType = "redis";
        this.l1MaxSize = 10000L;
        this.l2MaxSize = 100000L;
        this.l1DefaultTtl = Duration.ofMinutes(30);
        this.l2DefaultTtl = Duration.ofHours(2);
        this.consistencyCheckInterval = Duration.ofMinutes(10);
        this.consistencyCheckEnabled = false;
    }
    
    /**
     * 创建多级缓存配置Builder
     * 
     * @return Builder实例
     */
    public static Builder multilevelBuilder() {
        return new Builder();
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private final MultilevelCacheConfiguration config = new MultilevelCacheConfiguration();
        
        public Builder l1CacheType(String l1CacheType) {
            config.l1CacheType = l1CacheType;
            return this;
        }
        
        public Builder l2CacheType(String l2CacheType) {
            config.l2CacheType = l2CacheType;
            return this;
        }
        
        public Builder l1MaxSize(long l1MaxSize) {
            config.l1MaxSize = l1MaxSize;
            return this;
        }
        
        public Builder l2MaxSize(long l2MaxSize) {
            config.l2MaxSize = l2MaxSize;
            return this;
        }
        
        public Builder l1DefaultTtl(Duration l1DefaultTtl) {
            config.l1DefaultTtl = l1DefaultTtl;
            return this;
        }
        
        public Builder l2DefaultTtl(Duration l2DefaultTtl) {
            config.l2DefaultTtl = l2DefaultTtl;
            return this;
        }
        
        public Builder writeStrategy(WriteStrategy writeStrategy) {
            config.writeStrategy = writeStrategy;
            return this;
        }
        
        public Builder l1WriteBackEnabled(boolean l1WriteBackEnabled) {
            config.l1WriteBackEnabled = l1WriteBackEnabled;
            return this;
        }
        
        public Builder strictHealthCheck(boolean strictHealthCheck) {
            config.strictHealthCheck = strictHealthCheck;
            return this;
        }
        
        public MultilevelCacheConfiguration build() {
            return config;
        }
    }
    
    /**
     * 获取L1缓存配置
     * 
     * @return L1缓存配置
     */
    public DefaultCacheConfiguration getL1Config() {
        if (l1Config == null) {
            l1Config = DefaultCacheConfiguration.builder()
                .cacheType(l1CacheType)
                .maxSize(l1MaxSize)
                .defaultTtl(l1DefaultTtl)
                .enabled(isEnabled())
                .metricsEnabled(isMetricsEnabled())
                .traceEnabled(isTraceEnabled())
                .build();
        }
        return l1Config;
    }
    
    /**
     * 获取L2缓存配置
     * 
     * @return L2缓存配置
     */
    public DefaultCacheConfiguration getL2Config() {
        if (l2Config == null) {
            l2Config = DefaultCacheConfiguration.builder()
                .cacheType(l2CacheType)
                .maxSize(l2MaxSize)
                .defaultTtl(l2DefaultTtl)
                .enabled(isEnabled())
                .metricsEnabled(isMetricsEnabled())
                .traceEnabled(isTraceEnabled())
                .build();
        }
        return l2Config;
    }
    
    @Override
    public String getConfigPrefix() {
        return "vrml.cache.multilevel";
    }
}