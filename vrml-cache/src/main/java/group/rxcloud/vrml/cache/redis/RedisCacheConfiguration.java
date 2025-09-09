package group.rxcloud.vrml.cache.redis;

import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;

/**
 * Redis缓存配置
 * 
 * @author VRML Team
 * @since 1.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedisCacheConfiguration extends DefaultCacheConfiguration {
    
    /**
     * Redis连接超时时间
     */
    private Duration connectionTimeout = Duration.ofSeconds(5);
    
    /**
     * Redis读取超时时间
     */
    private Duration readTimeout = Duration.ofSeconds(3);
    
    /**
     * Redis连接池最大连接数
     */
    private int maxConnections = 100;
    
    /**
     * Redis连接池最小空闲连接数
     */
    private int minIdleConnections = 10;
    
    /**
     * Redis连接池最大空闲连接数
     */
    private int maxIdleConnections = 20;
    
    /**
     * Redis数据库索引
     */
    private int database = 0;
    
    /**
     * Redis密码
     */
    private String password;
    
    /**
     * Redis主机地址
     */
    private String host = "localhost";
    
    /**
     * Redis端口
     */
    private int port = 6379;
    
    /**
     * 是否启用SSL
     */
    private boolean sslEnabled = false;
    
    /**
     * 键前缀
     */
    private String keyPrefix = "vrml:cache:";
    
    /**
     * 是否启用键过期事件监听
     */
    private boolean keyExpirationEnabled = false;
    
    /**
     * 构造函数
     */
    public RedisCacheConfiguration() {
        super();
        setCacheType("redis");
        this.connectionTimeout = Duration.ofSeconds(5);
        this.readTimeout = Duration.ofSeconds(3);
        this.maxConnections = 100;
        this.minIdleConnections = 10;
        this.maxIdleConnections = 20;
        this.database = 0;
        this.host = "localhost";
        this.port = 6379;
        this.sslEnabled = false;
        this.keyPrefix = "vrml:cache:";
        this.keyExpirationEnabled = false;
    }
    
    /**
     * 创建Redis缓存配置Builder
     * 
     * @return Builder实例
     */
    public static Builder redisBuilder() {
        return new Builder();
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private final RedisCacheConfiguration config = new RedisCacheConfiguration();
        
        public Builder host(String host) {
            config.host = host;
            return this;
        }
        
        public Builder port(int port) {
            config.port = port;
            return this;
        }
        
        public Builder database(int database) {
            config.database = database;
            return this;
        }
        
        public Builder password(String password) {
            config.password = password;
            return this;
        }
        
        public Builder connectionTimeout(Duration connectionTimeout) {
            config.connectionTimeout = connectionTimeout;
            return this;
        }
        
        public Builder readTimeout(Duration readTimeout) {
            config.readTimeout = readTimeout;
            return this;
        }
        
        public Builder maxConnections(int maxConnections) {
            config.maxConnections = maxConnections;
            return this;
        }
        
        public Builder keyPrefix(String keyPrefix) {
            config.keyPrefix = keyPrefix;
            return this;
        }
        
        public Builder defaultTtl(Duration defaultTtl) {
            config.setDefaultTtl(defaultTtl);
            return this;
        }
        
        public Builder maxSize(long maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }
        
        public Builder avalancheProtectionEnabled(boolean enabled) {
            config.setAvalancheProtectionEnabled(enabled);
            return this;
        }
        
        public Builder hotKeyProtectionEnabled(boolean enabled) {
            config.setHotKeyProtectionEnabled(enabled);
            return this;
        }
        
        public RedisCacheConfiguration build() {
            return config;
        }
    }
    
    @Override
    public String getConfigPrefix() {
        return "vrml.cache.redis";
    }
}