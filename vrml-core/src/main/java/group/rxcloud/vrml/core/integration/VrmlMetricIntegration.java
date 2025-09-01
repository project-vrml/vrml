package group.rxcloud.vrml.core.integration;

import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

/**
 * VRML监控集成工具
 * 提供统一的监控指标收集接口
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlMetricIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlMetricIntegration.class);
    
    private static volatile Boolean metricsAvailable = null;
    
    /**
     * 记录操作执行时间
     * 
     * @param metricName 指标名称
     * @param operation 操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public static <T> Try<T> recordTime(String metricName, Supplier<Try<T>> operation) {
        if (metricName == null || metricName.trim().isEmpty()) {
            return operation.get();
        }
        
        Instant start = Instant.now();
        Try<T> result = Try.success(null);
        
        try {
            result = operation.get();
            return result;
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            recordMetric(metricName, duration, result.isSuccess());
        }
    }
    
    /**
     * 记录操作计数
     * 
     * @param metricName 指标名称
     * @param success 是否成功
     */
    public static void recordCount(String metricName, boolean success) {
        recordMetric(metricName + ".count", null, success);
    }
    
    /**
     * 记录指标
     * 
     * @param metricName 指标名称
     * @param duration 执行时间（可为null）
     * @param success 是否成功
     */
    private static void recordMetric(String metricName, Duration duration, boolean success) {
        if (!isMetricsAvailable()) {
            // 如果vrml-metric不可用，使用日志记录
            if (duration != null) {
                log.debug("[VRML] Metric: {} = {}ms, success = {}", metricName, duration.toMillis(), success);
            } else {
                log.debug("[VRML] Metric: {} count, success = {}", metricName, success);
            }
            return;
        }
        
        try {
            // 使用反射调用vrml-metric模块
            Class<?> metricsClass = Class.forName("group.rxcloud.vrml.metric.Metrics");
            
            // 简化的指标记录，只记录基本信息
            if (duration != null) {
                // 记录时间指标
                metricsClass.getMethod("time", String.class, long.class)
                    .invoke(null, metricName, duration.toMillis());
            } else {
                // 记录计数指标
                metricsClass.getMethod("count", String.class, boolean.class)
                    .invoke(null, metricName, success);
            }
            
        } catch (Exception e) {
            log.debug("[VRML] Failed to record metric via reflection: {}", metricName, e);
            // 降级到日志记录
            if (duration != null) {
                log.debug("[VRML] Metric: {} = {}ms, success = {}", metricName, duration.toMillis(), success);
            } else {
                log.debug("[VRML] Metric: {} count, success = {}", metricName, success);
            }
        }
    }
    
    /**
     * 检查vrml-metric模块是否可用
     * 
     * @return true表示可用，false表示不可用
     */
    private static boolean isMetricsAvailable() {
        if (metricsAvailable == null) {
            synchronized (VrmlMetricIntegration.class) {
                if (metricsAvailable == null) {
                    try {
                        Class.forName("group.rxcloud.vrml.metric.Metrics");
                        metricsAvailable = true;
                        log.debug("[VRML] vrml-metric module is available");
                    } catch (ClassNotFoundException e) {
                        metricsAvailable = false;
                        log.debug("[VRML] vrml-metric module is not available");
                    }
                }
            }
        }
        return metricsAvailable;
    }
    
    /**
     * 检查是否启用监控
     * 
     * @return true表示启用，false表示禁用
     */
    public static boolean isMetricsEnabled() {
        return VrmlIntegrationManager.getConfig().isMetricsEnabled() && isMetricsAvailable();
    }
}