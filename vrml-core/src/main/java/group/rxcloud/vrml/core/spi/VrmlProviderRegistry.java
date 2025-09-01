package group.rxcloud.vrml.core.spi;

import group.rxcloud.vrml.core.api.VrmlConfiguration;
import group.rxcloud.vrml.core.api.VrmlOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * VRML服务提供者注册表
 * 管理所有的服务提供者，支持SPI自动发现和手动注册
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlProviderRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlProviderRegistry.class);
    
    /**
     * 提供者缓存，按类型分组
     */
    private static final Map<Class<?>, List<VrmlProvider<?, ?>>> PROVIDERS = new ConcurrentHashMap<>();
    
    /**
     * 默认提供者缓存
     */
    private static final Map<Class<?>, VrmlProvider<?, ?>> DEFAULT_PROVIDERS = new ConcurrentHashMap<>();
    
    /**
     * 注册提供者
     * 
     * @param providerClass 提供者接口类
     * @param provider 提供者实例
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     */
    public static <T extends VrmlOperations, C extends VrmlConfiguration> void registerProvider(
            Class<? extends VrmlProvider<T, C>> providerClass, VrmlProvider<T, C> provider) {
        
        PROVIDERS.computeIfAbsent(providerClass, k -> new ArrayList<>()).add(provider);
        
        // 按优先级排序
        PROVIDERS.get(providerClass).sort(Comparator.comparingInt(VrmlProvider::getPriority));
        
        log.info("[VRML] Registered provider: {} for type: {}", provider.getName(), providerClass.getSimpleName());
    }
    
    /**
     * 通过SPI自动发现并注册提供者
     * 
     * @param providerClass 提供者接口类
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     */
    @SuppressWarnings("unchecked")
    public static <T extends VrmlOperations, C extends VrmlConfiguration> void discoverProviders(
            Class<? extends VrmlProvider<T, C>> providerClass) {
        
        ServiceLoader<? extends VrmlProvider<T, C>> serviceLoader = ServiceLoader.load(providerClass);
        
        for (VrmlProvider<T, C> provider : serviceLoader) {
            registerProvider(providerClass, provider);
        }
    }
    
    /**
     * 获取指定类型的所有提供者
     * 
     * @param providerClass 提供者接口类
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     * @return 提供者列表
     */
    @SuppressWarnings("unchecked")
    public static <T extends VrmlOperations, C extends VrmlConfiguration> List<VrmlProvider<T, C>> getProviders(
            Class<? extends VrmlProvider<T, C>> providerClass) {
        
        return PROVIDERS.getOrDefault(providerClass, Collections.emptyList())
                .stream()
                .map(provider -> (VrmlProvider<T, C>) provider)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定类型和名称的提供者
     * 
     * @param providerClass 提供者接口类
     * @param type 提供者类型
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     * @return 提供者实例，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T extends VrmlOperations, C extends VrmlConfiguration> VrmlProvider<T, C> getProvider(
            Class<? extends VrmlProvider<T, C>> providerClass, String type) {
        
        return PROVIDERS.getOrDefault(providerClass, Collections.emptyList())
                .stream()
                .filter(provider -> provider.supports(type))
                .map(provider -> (VrmlProvider<T, C>) provider)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取默认提供者
     * 
     * @param providerClass 提供者接口类
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     * @return 默认提供者实例，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T extends VrmlOperations, C extends VrmlConfiguration> VrmlProvider<T, C> getDefaultProvider(
            Class<? extends VrmlProvider<T, C>> providerClass) {
        
        VrmlProvider<?, ?> defaultProvider = DEFAULT_PROVIDERS.get(providerClass);
        if (defaultProvider != null) {
            return (VrmlProvider<T, C>) defaultProvider;
        }
        
        // 如果没有设置默认提供者，返回优先级最高的
        List<VrmlProvider<T, C>> providers = getProviders(providerClass);
        return providers.isEmpty() ? null : providers.get(0);
    }
    
    /**
     * 设置默认提供者
     * 
     * @param providerClass 提供者接口类
     * @param provider 提供者实例
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     */
    public static <T extends VrmlOperations, C extends VrmlConfiguration> void setDefaultProvider(
            Class<? extends VrmlProvider<T, C>> providerClass, VrmlProvider<T, C> provider) {
        
        DEFAULT_PROVIDERS.put(providerClass, provider);
        log.info("[VRML] Set default provider: {} for type: {}", provider.getName(), providerClass.getSimpleName());
    }
    
    /**
     * 获取所有健康的提供者
     * 
     * @param providerClass 提供者接口类
     * @param <T> 操作接口类型
     * @param <C> 配置接口类型
     * @return 健康的提供者列表
     */
    public static <T extends VrmlOperations, C extends VrmlConfiguration> List<VrmlProvider<T, C>> getHealthyProviders(
            Class<? extends VrmlProvider<T, C>> providerClass) {
        
        return getProviders(providerClass)
                .stream()
                .filter(VrmlProvider::isHealthy)
                .collect(Collectors.toList());
    }
    
    /**
     * 清空所有提供者（主要用于测试）
     */
    public static void clear() {
        PROVIDERS.clear();
        DEFAULT_PROVIDERS.clear();
    }
}