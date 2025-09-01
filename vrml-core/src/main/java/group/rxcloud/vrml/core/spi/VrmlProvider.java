package group.rxcloud.vrml.core.spi;

import group.rxcloud.vrml.core.api.VrmlConfiguration;
import group.rxcloud.vrml.core.api.VrmlOperations;

/**
 * VRML服务提供者接口
 * 使用SPI机制实现可插拔的适配器
 * 
 * @param <T> 操作接口类型
 * @param <C> 配置接口类型
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface VrmlProvider<T extends VrmlOperations, C extends VrmlConfiguration> {
    
    /**
     * 获取提供者名称
     * 
     * @return 提供者名称
     */
    String getName();
    
    /**
     * 获取提供者类型
     * 
     * @return 提供者类型
     */
    String getType();
    
    /**
     * 创建操作实例
     * 
     * @param pattern 模式字符串（如缓存键模式）
     * @param config 配置对象
     * @return 操作实例
     */
    T createOperations(String pattern, C config);
    
    /**
     * 是否支持指定的类型
     * 
     * @param type 类型
     * @return true表示支持，false表示不支持
     */
    boolean supports(String type);
    
    /**
     * 获取优先级，数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * 健康检查
     * 
     * @return true表示健康，false表示不健康
     */
    default boolean isHealthy() {
        return true;
    }
}