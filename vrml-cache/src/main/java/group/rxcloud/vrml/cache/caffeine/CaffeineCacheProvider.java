package group.rxcloud.vrml.cache.caffeine;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.spi.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caffeine本地缓存提供者
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CaffeineCacheProvider implements CacheProvider {
    
    private static final Logger log = LoggerFactory.getLogger(CaffeineCacheProvider.class);
    
    @Override
    public String getName() {
        return "Caffeine Cache Provider";
    }
    
    @Override
    public String getType() {
        return Types.CAFFEINE;
    }
    
    @Override
    public CacheOperations createOperations(String pattern, CacheConfiguration config) {
        log.debug("[VRML-Cache] Creating Caffeine cache operations: pattern={}", pattern);
        return new CaffeineCacheOperations(pattern, config);
    }
    
    @Override
    public boolean supports(String type) {
        return Types.CAFFEINE.equals(type) || Types.LOCAL.equals(type);
    }
    
    @Override
    public int getPriority() {
        return 20; // 中等优先级
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // Caffeine是本地缓存，通常总是健康的
            // 可以通过创建一个临时缓存来验证
            com.github.benmanes.caffeine.cache.Cache<String, String> testCache = 
                com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                    .maximumSize(1)
                    .build();
            
            testCache.put("test", "value");
            String value = testCache.getIfPresent("test");
            return "value".equals(value);
        } catch (Exception e) {
            log.warn("[VRML-Cache] Caffeine health check failed", e);
            return false;
        }
    }
}