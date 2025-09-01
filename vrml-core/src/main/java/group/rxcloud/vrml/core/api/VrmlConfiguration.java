package group.rxcloud.vrml.core.api;

/**
 * VRML配置接口基类
 * 提供统一的配置接口规范
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface VrmlConfiguration {
    
    /**
     * 是否启用该模块
     * 
     * @return true表示启用，false表示禁用
     */
    default boolean isEnabled() {
        return true;
    }
    
    /**
     * 是否启用监控
     * 
     * @return true表示启用监控，false表示禁用监控
     */
    default boolean isMetricsEnabled() {
        return true;
    }
    
    /**
     * 是否启用链路追踪
     * 
     * @return true表示启用追踪，false表示禁用追踪
     */
    default boolean isTraceEnabled() {
        return true;
    }
    
    /**
     * 获取配置前缀
     * 
     * @return 配置前缀
     */
    String getConfigPrefix();
}