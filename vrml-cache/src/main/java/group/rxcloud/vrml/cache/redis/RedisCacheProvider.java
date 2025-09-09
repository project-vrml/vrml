package group.rxcloud.vrml.cache.redis;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.spi.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis缓存提供者
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class RedisCacheProvider implements CacheProvider {
    
    private static final Logger log = LoggerFactory.getLogger(RedisCacheProvider.class);
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 构造函数
     * 
     * @param redisTemplate Redis模板
     */
    public RedisCacheProvider(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public String getName() {
        return "Redis Cache Provider";
    }
    
    @Override
    public String getType() {
        return Types.REDIS;
    }
    
    @Override
    public CacheOperations createOperations(String pattern, CacheConfiguration config) {
        log.debug("[VRML-Cache] Creating Redis cache operations: pattern={}", pattern);
        return new RedisCacheOperations(pattern, config, redisTemplate);
    }
    
    @Override
    public boolean supports(String type) {
        return Types.REDIS.equals(type);
    }
    
    @Override
    public int getPriority() {
        return 10; // 高优先级
    }
    
    @Override
    public boolean isHealthy() {
        try {
            if (redisTemplate == null || redisTemplate.getConnectionFactory() == null) {
                return false;
            }
            
            String result = redisTemplate.getConnectionFactory().getConnection().ping();
            return "PONG".equals(result);
        } catch (Exception e) {
            log.warn("[VRML-Cache] Redis health check failed", e);
            return false;
        }
    }
}