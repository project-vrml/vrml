package group.rxcloud.vrml.core.api;

import group.rxcloud.vrml.core.integration.VrmlAlertIntegration;
import group.rxcloud.vrml.core.integration.VrmlLogIntegration;
import group.rxcloud.vrml.core.integration.VrmlMetricIntegration;
import group.rxcloud.vrml.core.integration.VrmlTraceIntegration;
import io.vavr.control.Try;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * VRML操作抽象基类
 * 提供通用的监控、日志、告警集成功能
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public abstract class AbstractVrmlOperations implements VrmlOperations {
    
    protected String metricName;
    protected String traceKey;
    protected Consumer<Throwable> errorHandler;
    
    /**
     * 构造函数
     */
    protected AbstractVrmlOperations() {
        // 默认构造函数
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 其他实例
     */
    protected AbstractVrmlOperations(AbstractVrmlOperations other) {
        this.metricName = other.metricName;
        this.traceKey = other.traceKey;
        this.errorHandler = other.errorHandler;
    }
    
    @Override
    public VrmlOperations withMetrics(String metricName) {
        AbstractVrmlOperations copy = createCopy();
        copy.metricName = metricName;
        return copy;
    }
    
    @Override
    public VrmlOperations withTrace(String traceKey) {
        AbstractVrmlOperations copy = createCopy();
        copy.traceKey = traceKey;
        return copy;
    }
    
    @Override
    public VrmlOperations onError(Consumer<Throwable> errorHandler) {
        AbstractVrmlOperations copy = createCopy();
        copy.errorHandler = errorHandler;
        return copy;
    }
    
    /**
     * 创建当前实例的副本
     * 子类需要实现此方法
     * 
     * @return 实例副本
     */
    protected abstract AbstractVrmlOperations createCopy();
    
    /**
     * 执行操作并集成监控、日志、告警、链路追踪
     * 
     * @param operationName 操作名称
     * @param operation 操作逻辑
     * @param <T> 返回类型
     * @return 操作结果
     */
    protected <T> Try<T> executeWithIntegration(String operationName, Supplier<Try<T>> operation) {
        // 在追踪上下文中执行操作
        return VrmlTraceIntegration.withTrace(traceKey != null ? traceKey + "." + operationName : operationName, () -> {
            // 记录开始日志
            if (traceKey != null) {
                VrmlLogIntegration.debug(traceKey, "Starting operation: {}", operationName);
            }
            
            // 设置追踪标签
            if (traceKey != null) {
                VrmlTraceIntegration.setTag("operation", operationName);
                if (metricName != null) {
                    VrmlTraceIntegration.setTag("metric", metricName);
                }
            }
            
            // 执行操作并记录指标
            Try<T> result;
            if (metricName != null) {
                result = VrmlMetricIntegration.recordTime(metricName + "." + operationName, operation);
            } else {
                result = operation.get();
            }
            
            // 处理结果
            if (result.isSuccess()) {
                // 记录成功日志
                if (traceKey != null) {
                    VrmlLogIntegration.debug(traceKey, "Operation completed successfully: {}", operationName);
                    VrmlTraceIntegration.setTag("result", "success");
                }
            } else {
                // 记录失败日志
                Throwable throwable = result.getCause();
                if (traceKey != null) {
                    VrmlLogIntegration.error(traceKey, "Operation failed: " + operationName, throwable);
                    VrmlTraceIntegration.setTag("result", "failure");
                    VrmlTraceIntegration.setTag("error", throwable.getMessage());
                }
                
                // 触发告警
                if (metricName != null) {
                    VrmlAlertIntegration.alert(metricName + ".error", 
                        "Operation failed: " + operationName, throwable);
                }
                
                // 调用错误处理器
                if (errorHandler != null) {
                    try {
                        errorHandler.accept(throwable);
                    } catch (Exception e) {
                        VrmlLogIntegration.error("vrml.error.handler", 
                            "Error handler failed for operation: " + operationName, e);
                    }
                }
            }
            
            return result;
        });
    }
    
    /**
     * 记录操作计数
     * 
     * @param operationName 操作名称
     * @param success 是否成功
     */
    protected void recordCount(String operationName, boolean success) {
        if (metricName != null) {
            VrmlMetricIntegration.recordCount(metricName + "." + operationName, success);
        }
    }
    
    /**
     * 记录操作日志
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @param args 参数
     */
    protected void log(LogLevel level, String message, Object... args) {
        if (traceKey == null) {
            return;
        }
        
        switch (level) {
            case DEBUG:
                VrmlLogIntegration.debug(traceKey, message, args);
                break;
            case INFO:
                VrmlLogIntegration.info(traceKey, message, args);
                break;
            case WARN:
                VrmlLogIntegration.warn(traceKey, message, args);
                break;
            case ERROR:
                if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
                    Object[] messageArgs = new Object[args.length - 1];
                    System.arraycopy(args, 0, messageArgs, 0, messageArgs.length);
                    String formattedMessage = messageArgs.length > 0 ? 
                        String.format(message, messageArgs) : message;
                    VrmlLogIntegration.error(traceKey, formattedMessage, (Throwable) args[args.length - 1]);
                } else {
                    VrmlLogIntegration.error(traceKey, String.format(message, args), null);
                }
                break;
        }
    }
    
    /**
     * 日志级别枚举
     */
    protected enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}