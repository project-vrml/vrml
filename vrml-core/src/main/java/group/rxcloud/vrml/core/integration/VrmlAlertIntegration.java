package group.rxcloud.vrml.core.integration;

import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VRML告警集成工具
 * 提供统一的告警触发接口
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlAlertIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlAlertIntegration.class);
    
    /**
     * 触发告警
     * 
     * @param alertKey 告警键
     * @param message 告警消息
     * @param throwable 异常（可为null）
     */
    public static void alert(String alertKey, String message, Throwable throwable) {
        try {
            // 尝试使用vrml-alert模块触发告警
            Class<?> alertsClass = Class.forName("group.rxcloud.vrml.alert.Alerts");
            Class<?> messageClass = Class.forName("group.rxcloud.vrml.alert.message.AlertMessage");
            
            // 创建告警消息对象
            Object alertMessage = messageClass.getConstructor(String.class, String.class, Throwable.class)
                    .newInstance(alertKey, message, throwable);
            
            // 触发告警
            alertsClass.getMethod("tell", messageClass).invoke(null, alertMessage);
            
        } catch (Exception e) {
            // 如果vrml-alert不可用，使用日志记录
            if (throwable != null) {
                log.error("[ALERT][{}] {}", alertKey, message, throwable);
            } else {
                log.error("[ALERT][{}] {}", alertKey, message);
            }
        }
    }
    
    /**
     * 触发告警（无异常）
     * 
     * @param alertKey 告警键
     * @param message 告警消息
     */
    public static void alert(String alertKey, String message) {
        alert(alertKey, message, null);
    }
    
    /**
     * 触发性能告警
     * 
     * @param operation 操作名称
     * @param duration 执行时间（毫秒）
     * @param threshold 阈值（毫秒）
     */
    public static void performanceAlert(String operation, long duration, long threshold) {
        if (duration > threshold) {
            alert("performance.slow", 
                String.format("Operation [%s] took %dms, exceeding threshold %dms", 
                    operation, duration, threshold));
        }
    }
    
    /**
     * 触发错误率告警
     * 
     * @param operation 操作名称
     * @param errorCount 错误次数
     * @param totalCount 总次数
     * @param threshold 错误率阈值（0-1之间）
     */
    public static void errorRateAlert(String operation, int errorCount, int totalCount, double threshold) {
        if (totalCount > 0) {
            double errorRate = (double) errorCount / totalCount;
            if (errorRate > threshold) {
                alert("error.rate.high", 
                    String.format("Operation [%s] error rate %.2f%% exceeds threshold %.2f%%", 
                        operation, errorRate * 100, threshold * 100));
            }
        }
    }
    
    /**
     * 检查是否启用告警
     * 
     * @return true表示启用，false表示禁用
     */
    public static boolean isAlertEnabled() {
        try {
            // 尝试从Spring上下文获取配置
            return SpringContextConfigurator.getApplicationContext() != null;
        } catch (Exception e) {
            return false;
        }
    }
}