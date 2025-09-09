package group.rxcloud.vrml.cache;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import group.rxcloud.vrml.cache.spi.CacheProvider;
import group.rxcloud.vrml.core.spi.VrmlProvider;
import group.rxcloud.vrml.core.spi.VrmlProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存操作入口类
 * 提供统一的缓存API，支持多种缓存实现
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class Caches {
    
    private static final Logger log = LoggerFactory.getLogger(Caches.class);
    
    /**
     * 缓存实例缓存
     */
    private static final ConcurrentMap<String, CacheOperations> CACHE_INSTANCES = new ConcurrentHashMap<>();
    
    /**
     * 默认配置
     */
    private static CacheConfiguration defaultConfiguration = new DefaultCacheConfiguration();
    
    static {
        // 自动发现缓存提供者
        VrmlProviderRegistry.discoverProviders(CacheProvider.class);
    }
    
    /**
     * 获取Redis缓存操作
     * 
     * @param pattern 缓存键模式
     * @return Redis缓存操作
     */
    public static CacheOperations redis(String pattern) {
        return getCache(CacheProvider.Types.REDIS, pattern, defaultConfiguration);
    }
    
    /**
     * 获取Redis缓存操作
     * 
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @return Redis缓存操作
     */
    public static CacheOperations redis(String pattern, CacheConfiguration config) {
        return getCache(CacheProvider.Types.REDIS, pattern, config);
    }
    
    /**
     * 获取本地缓存操作
     * 
     * @param pattern 缓存键模式
     * @return 本地缓存操作
     */
    public static CacheOperations local(String pattern) {
        return getCache(CacheProvider.Types.CAFFEINE, pattern, defaultConfiguration);
    }
    
    /**
     * 获取本地缓存操作
     * 
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @return 本地缓存操作
     */
    public static CacheOperations local(String pattern, CacheConfiguration config) {
        return getCache(CacheProvider.Types.CAFFEINE, pattern, config);
    }
    
    /**
     * 获取多级缓存操作
     * 
     * @param pattern 缓存键模式
     * @return 多级缓存操作
     */
    public static CacheOperations multilevel(String pattern) {
        return getCache(CacheProvider.Types.MULTILEVEL, pattern, defaultConfiguration);
    }
    
    /**
     * 获取多级缓存操作
     * 
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @return 多级缓存操作
     */
    public static CacheOperations multilevel(String pattern, CacheConfiguration config) {
        return getCache(CacheProvider.Types.MULTILEVEL, pattern, config);
    }
    
    /**
     * 获取指定类型的缓存操作
     * 
     * @param type 缓存类型
     * @param pattern 缓存键模式
     * @return 缓存操作
     */
    public static CacheOperations of(String type, String pattern) {
        return getCache(type, pattern, defaultConfiguration);
    }
    
    /**
     * 获取指定类型的缓存操作
     * 
     * @param type 缓存类型
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @return 缓存操作
     */
    public static CacheOperations of(String type, String pattern, CacheConfiguration config) {
        return getCache(type, pattern, config);
    }
    
    /**
     * 设置默认配置
     * 
     * @param configuration 默认配置
     */
    public static void setDefaultConfiguration(CacheConfiguration configuration) {
        defaultConfiguration = configuration;
    }
    
    /**
     * 获取默认配置
     * 
     * @return 默认配置
     */
    public static CacheConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }
    
    /**
     * 清空缓存实例缓存（主要用于测试）
     */
    public static void clearInstances() {
        CACHE_INSTANCES.clear();
    }
    
    /**
     * 获取缓存操作实例
     * 
     * @param type 缓存类型
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @return 缓存操作实例
     */
    private static CacheOperations getCache(String type, String pattern, CacheConfiguration config) {
        // 使用更稳定的缓存键生成策略
        String cacheKey = generateCacheKey(type, pattern, config);
        
        return CACHE_INSTANCES.computeIfAbsent(cacheKey, key -> {
            VrmlProvider<CacheOperations, CacheConfiguration> provider = VrmlProviderRegistry.getProvider(CacheProvider.class, type);
            if (provider == null) {
                throw new IllegalArgumentException("No cache provider found for type: " + type);
            }
            
            log.debug("[VRML-Cache] Creating cache instance: type={}, pattern={}, provider={}", 
                type, pattern, provider.getName());
            
            return provider.createOperations(pattern, config);
        });
    }
    
    /**
     * 格式化缓存键
     * 
     * @param pattern 键模式
     * @param args 参数
     * @return 格式化后的键
     */
    public static String formatKey(String pattern, Object... args) {
        if (args == null || args.length == 0) {
            return pattern;
        }
        
        // 简单的字符串格式化，支持 {} 占位符
        String result = pattern;
        for (Object arg : args) {
            result = result.replaceFirst("\\{\\}", String.valueOf(arg));
        }
        return result;
    }
    
    /**
     * 生成稳定的缓存键
     * 
     * @param type 缓存类型
     * @param pattern 键模式
     * @param config 配置
     * @return 缓存键
     */
    private static String generateCacheKey(String type, String pattern, CacheConfiguration config) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(type).append(":");
        keyBuilder.append(pattern).append(":");
        
        // 使用配置的关键属性而不是hashCode，确保稳定性
        keyBuilder.append(config.getCacheType()).append(":");
        keyBuilder.append(config.getMaxSize()).append(":");
        if (config.getDefaultTtl() != null) {
            keyBuilder.append(config.getDefaultTtl().toMillis());
        }
        
        return keyBuilder.toString();
    }
}