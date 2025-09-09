package group.rxcloud.vrml.cache.spi;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.core.spi.VrmlProvider;

/**
 * 缓存提供者接口
 * 使用SPI机制实现可插拔的缓存适配器
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface CacheProvider extends VrmlProvider<CacheOperations, CacheConfiguration> {
    
    /**
     * 缓存类型常量
     */
    interface Types {
        String REDIS = "redis";
        String CAFFEINE = "caffeine";
        String LOCAL = "local";
        String MULTILEVEL = "multilevel";
        String GUAVA = "guava";
    }
}