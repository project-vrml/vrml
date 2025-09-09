package group.rxcloud.vrml.cache.multilevel;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.spi.CacheProvider;
import group.rxcloud.vrml.core.spi.VrmlProvider;
import group.rxcloud.vrml.core.spi.VrmlProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多级缓存提供者
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class MultilevelCacheProvider implements CacheProvider {
    
    private static final Logger log = LoggerFactory.getLogger(MultilevelCacheProvider.class);
    
    @Override
    public String getName() {
        return "Multilevel Cache Provider";
    }
    
    @Override
    public String getType() {
        return Types.MULTILEVEL;
    }
    
    @Override
    public CacheOperations createOperations(String pattern, CacheConfiguration config) {
        log.debug("[VRML-Cache] Creating multilevel cache operations: pattern={}", pattern);
        
        MultilevelCacheConfiguration multilevelConfig = config instanceof MultilevelCacheConfiguration 
            ? (MultilevelCacheConfiguration) config 
            : new MultilevelCacheConfiguration();
        
        // 创建L1缓存（本地缓存）
        VrmlProvider<CacheOperations, CacheConfiguration> l1VrmlProvider = VrmlProviderRegistry.getProvider(CacheProvider.class, multilevelConfig.getL1CacheType());
        if (l1VrmlProvider == null) {
            throw new IllegalStateException("No L1 cache provider found for type: " + multilevelConfig.getL1CacheType());
        }
        CacheProvider l1Provider = (CacheProvider) l1VrmlProvider;
        
        CacheOperations l1Cache = l1Provider.createOperations(pattern, multilevelConfig.getL1Config());
        log.debug("[VRML-Cache] Created L1 cache: type={}, provider={}", 
            multilevelConfig.getL1CacheType(), l1Provider.getName());
        
        // 创建L2缓存（远程缓存）
        VrmlProvider<CacheOperations, CacheConfiguration> l2VrmlProvider = VrmlProviderRegistry.getProvider(CacheProvider.class, multilevelConfig.getL2CacheType());
        if (l2VrmlProvider == null) {
            throw new IllegalStateException("No L2 cache provider found for type: " + multilevelConfig.getL2CacheType());
        }
        CacheProvider l2Provider = (CacheProvider) l2VrmlProvider;
        
        CacheOperations l2Cache = l2Provider.createOperations(pattern, multilevelConfig.getL2Config());
        log.debug("[VRML-Cache] Created L2 cache: type={}, provider={}", 
            multilevelConfig.getL2CacheType(), l2Provider.getName());
        
        return new MultilevelCacheOperations(pattern, multilevelConfig, l1Cache, l2Cache);
    }
    
    @Override
    public boolean supports(String type) {
        return Types.MULTILEVEL.equals(type);
    }
    
    @Override
    public int getPriority() {
        return 5; // 最高优先级
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // 检查是否能找到L1和L2缓存提供者
            VrmlProvider<CacheOperations, CacheConfiguration> l1VrmlProvider = VrmlProviderRegistry.getProvider(CacheProvider.class, "caffeine");
            VrmlProvider<CacheOperations, CacheConfiguration> l2VrmlProvider = VrmlProviderRegistry.getProvider(CacheProvider.class, "redis");
            
            boolean l1Available = l1VrmlProvider != null && l1VrmlProvider.isHealthy();
            boolean l2Available = l2VrmlProvider != null && l2VrmlProvider.isHealthy();
            
            // 至少有一个缓存提供者可用就认为是健康的
            return l1Available || l2Available;
        } catch (Exception e) {
            log.warn("[VRML-Cache] Multilevel cache health check failed", e);
            return false;
        }
    }
}