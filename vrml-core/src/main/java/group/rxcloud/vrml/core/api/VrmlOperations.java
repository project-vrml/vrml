package group.rxcloud.vrml.core.api;

import io.vavr.control.Try;

/**
 * VRML操作接口基类
 * 提供统一的操作接口规范，包含监控和链路追踪集成
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface VrmlOperations extends VrmlApi {
    
    /**
     * 集成监控指标
     * 
     * @param metricName 指标名称
     * @return 带监控的操作接口
     */
    VrmlOperations withMetrics(String metricName);
    
    /**
     * 集成链路追踪
     * 
     * @param traceKey 追踪键
     * @return 带追踪的操作接口
     */
    VrmlOperations withTrace(String traceKey);
    
    /**
     * 错误处理回调
     * 
     * @param errorHandler 错误处理器
     * @return 带错误处理的操作接口
     */
    VrmlOperations onError(java.util.function.Consumer<Throwable> errorHandler);
    
    /**
     * 健康检查
     * 
     * @return 健康状态
     */
    default Try<Boolean> healthCheck() {
        return Try.success(true);
    }
}