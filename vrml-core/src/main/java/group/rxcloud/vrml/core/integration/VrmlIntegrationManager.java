package group.rxcloud.vrml.core.integration;

import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * VRML集成管理器
 * 统一管理监控、日志、告警等集成功能的初始化和配置
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlIntegrationManager {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlIntegrationManager.class);
    
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private static volatile boolean metricsEnabled = true;
    private static volatile boolean loggingEnabled = true;
    private static volatile boolean alertEnabled = true;
    private static volatile boolean traceEnabled = true;
    
    /**
     * 初始化集成管理器
     */
    public static void initialize() {
        if (initialized.compareAndSet(false, true)) {
            try {
                // 检查各个模块的可用性
                checkModuleAvailability();
                
                log.info("[VRML] Integration manager initialized successfully");
                log.info("[VRML] Metrics enabled: {}", metricsEnabled);
                log.info("[VRML] Logging enabled: {}", loggingEnabled);
                log.info("[VRML] Alert enabled: {}", alertEnabled);
                log.info("[VRML] Trace enabled: {}", traceEnabled);
                
            } catch (Exception e) {
                log.error("[VRML] Failed to initialize integration manager", e);
            }
        }
    }
    
    /**
     * 检查各个模块的可用性
     */
    private static void checkModuleAvailability() {
        // 检查vrml-metric模块
        try {
            Class.forName("group.rxcloud.vrml.metric.Metrics");
            metricsEnabled = true;
            log.debug("[VRML] vrml-metric module is available");
        } catch (ClassNotFoundException e) {
            metricsEnabled = false;
            log.debug("[VRML] vrml-metric module is not available");
        }
        
        // 检查vrml-log模块
        try {
            Class.forName("group.rxcloud.vrml.log.Logs");
            loggingEnabled = true;
            log.debug("[VRML] vrml-log module is available");
        } catch (ClassNotFoundException e) {
            loggingEnabled = false;
            log.debug("[VRML] vrml-log module is not available");
        }
        
        // 检查vrml-alert模块
        try {
            Class.forName("group.rxcloud.vrml.alert.Alerts");
            alertEnabled = true;
            log.debug("[VRML] vrml-alert module is available");
        } catch (ClassNotFoundException e) {
            alertEnabled = false;
            log.debug("[VRML] vrml-alert module is not available");
        }
        
        // 检查vrml-trace模块
        try {
            Class.forName("group.rxcloud.vrml.trace.Traces");
            traceEnabled = true;
            log.debug("[VRML] vrml-trace module is available");
        } catch (ClassNotFoundException e) {
            traceEnabled = false;
            log.debug("[VRML] vrml-trace module is not available");
        }
    }
    
    /**
     * 获取集成配置
     * 
     * @return 集成配置
     */
    public static IntegrationConfig getConfig() {
        if (!initialized.get()) {
            initialize();
        }
        
        return new IntegrationConfig(metricsEnabled, loggingEnabled, alertEnabled, traceEnabled);
    }
    
    /**
     * 设置监控启用状态
     * 
     * @param enabled 是否启用
     */
    public static void setMetricsEnabled(boolean enabled) {
        metricsEnabled = enabled;
        log.info("[VRML] Metrics enabled set to: {}", enabled);
    }
    
    /**
     * 设置日志启用状态
     * 
     * @param enabled 是否启用
     */
    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
        log.info("[VRML] Logging enabled set to: {}", enabled);
    }
    
    /**
     * 设置告警启用状态
     * 
     * @param enabled 是否启用
     */
    public static void setAlertEnabled(boolean enabled) {
        alertEnabled = enabled;
        log.info("[VRML] Alert enabled set to: {}", enabled);
    }
    
    /**
     * 设置链路追踪启用状态
     * 
     * @param enabled 是否启用
     */
    public static void setTraceEnabled(boolean enabled) {
        traceEnabled = enabled;
        log.info("[VRML] Trace enabled set to: {}", enabled);
    }
    
    /**
     * 检查Spring上下文是否可用
     * 
     * @return true表示可用，false表示不可用
     */
    public static boolean isSpringContextAvailable() {
        try {
            return SpringContextConfigurator.getApplicationContext() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 集成配置类
     */
    public static class IntegrationConfig {
        private final boolean metricsEnabled;
        private final boolean loggingEnabled;
        private final boolean alertEnabled;
        private final boolean traceEnabled;
        
        public IntegrationConfig(boolean metricsEnabled, boolean loggingEnabled, 
                               boolean alertEnabled, boolean traceEnabled) {
            this.metricsEnabled = metricsEnabled;
            this.loggingEnabled = loggingEnabled;
            this.alertEnabled = alertEnabled;
            this.traceEnabled = traceEnabled;
        }
        
        public boolean isMetricsEnabled() {
            return metricsEnabled;
        }
        
        public boolean isLoggingEnabled() {
            return loggingEnabled;
        }
        
        public boolean isAlertEnabled() {
            return alertEnabled;
        }
        
        public boolean isTraceEnabled() {
            return traceEnabled;
        }
        
        @Override
        public String toString() {
            return String.format("IntegrationConfig{metrics=%s, logging=%s, alert=%s, trace=%s}", 
                metricsEnabled, loggingEnabled, alertEnabled, traceEnabled);
        }
    }
}