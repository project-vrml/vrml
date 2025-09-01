package group.rxcloud.vrml.core.integration;

import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VRML日志集成工具
 * 提供统一的日志记录接口
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlLogIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlLogIntegration.class);
    
    private static volatile Boolean logsAvailable = null;
    
    /**
     * 记录操作日志
     * 
     * @param logKey 日志键
     * @param message 日志消息
     * @param args 参数
     */
    public static void info(String logKey, String message, Object... args) {
        if (isLogsAvailable()) {
            try {
                // 使用反射调用vrml-log模块
                Class<?> logsClass = Class.forName("group.rxcloud.vrml.log.Logs");
                Class<?> factoryClass = Class.forName("group.rxcloud.vrml.log.Logs$Factory");
                
                Object logs = factoryClass.getMethod("getLogs", Class.class)
                    .invoke(null, VrmlLogIntegration.class);
                Object keyedLogs = logs.getClass().getMethod("key", String.class)
                    .invoke(logs, logKey);
                keyedLogs.getClass().getMethod("info", String.class, Object[].class)
                    .invoke(keyedLogs, message, args);
                return;
            } catch (Exception e) {
                log.debug("[VRML] Failed to use vrml-log module", e);
            }
        }
        
        // 降级到标准日志
        log.info("[{}] {}", logKey, formatMessage(message, args));
    }
    
    /**
     * 记录错误日志
     * 
     * @param logKey 日志键
     * @param message 日志消息
     * @param throwable 异常
     */
    public static void error(String logKey, String message, Throwable throwable) {
        if (isLogsAvailable()) {
            try {
                // 使用反射调用vrml-log模块
                Class<?> logsClass = Class.forName("group.rxcloud.vrml.log.Logs");
                Class<?> factoryClass = Class.forName("group.rxcloud.vrml.log.Logs$Factory");
                
                Object logs = factoryClass.getMethod("getLogs", Class.class)
                    .invoke(null, VrmlLogIntegration.class);
                Object keyedLogs = logs.getClass().getMethod("key", String.class)
                    .invoke(logs, logKey);
                keyedLogs.getClass().getMethod("error", String.class, Throwable.class)
                    .invoke(keyedLogs, message, throwable);
                return;
            } catch (Exception e) {
                log.debug("[VRML] Failed to use vrml-log module", e);
            }
        }
        
        // 降级到标准日志
        log.error("[{}] {}", logKey, message, throwable);
    }
    
    /**
     * 记录警告日志
     * 
     * @param logKey 日志键
     * @param message 日志消息
     * @param args 参数
     */
    public static void warn(String logKey, String message, Object... args) {
        if (isLogsAvailable()) {
            try {
                // 使用反射调用vrml-log模块
                Class<?> logsClass = Class.forName("group.rxcloud.vrml.log.Logs");
                Class<?> factoryClass = Class.forName("group.rxcloud.vrml.log.Logs$Factory");
                
                Object logs = factoryClass.getMethod("getLogs", Class.class)
                    .invoke(null, VrmlLogIntegration.class);
                Object keyedLogs = logs.getClass().getMethod("key", String.class)
                    .invoke(logs, logKey);
                keyedLogs.getClass().getMethod("warn", String.class, Object[].class)
                    .invoke(keyedLogs, message, args);
                return;
            } catch (Exception e) {
                log.debug("[VRML] Failed to use vrml-log module", e);
            }
        }
        
        // 降级到标准日志
        log.warn("[{}] {}", logKey, formatMessage(message, args));
    }
    
    /**
     * 记录调试日志
     * 
     * @param logKey 日志键
     * @param message 日志消息
     * @param args 参数
     */
    public static void debug(String logKey, String message, Object... args) {
        if (isLogsAvailable()) {
            try {
                // 使用反射调用vrml-log模块
                Class<?> logsClass = Class.forName("group.rxcloud.vrml.log.Logs");
                Class<?> factoryClass = Class.forName("group.rxcloud.vrml.log.Logs$Factory");
                
                Object logs = factoryClass.getMethod("getLogs", Class.class)
                    .invoke(null, VrmlLogIntegration.class);
                Object keyedLogs = logs.getClass().getMethod("key", String.class)
                    .invoke(logs, logKey);
                keyedLogs.getClass().getMethod("debug", String.class, Object[].class)
                    .invoke(keyedLogs, message, args);
                return;
            } catch (Exception e) {
                log.debug("[VRML] Failed to use vrml-log module", e);
            }
        }
        
        // 降级到标准日志
        log.debug("[{}] {}", logKey, formatMessage(message, args));
    }
    
    /**
     * 使用标签记录日志
     * 
     * @param logKey 日志键
     * @param tagKey 标签键
     * @param tagValue 标签值
     * @param message 日志消息
     * @param args 参数
     */
    public static void infoWithTag(String logKey, String tagKey, String tagValue, String message, Object... args) {
        if (isLogsAvailable()) {
            try {
                // 使用反射调用vrml-log模块的标签功能
                Class<?> logsClass = Class.forName("group.rxcloud.vrml.log.Logs");
                Class<?> factoryClass = Class.forName("group.rxcloud.vrml.log.Logs$Factory");
                
                Object logs = factoryClass.getMethod("getLogs", Class.class)
                    .invoke(null, VrmlLogIntegration.class);
                Object keyedLogs = logs.getClass().getMethod("key", String.class)
                    .invoke(logs, logKey);
                Object taggedLogs = keyedLogs.getClass().getMethod("tag", String.class, String.class)
                    .invoke(keyedLogs, tagKey, tagValue);
                
                // 调用build方法并传入lambda
                // 这里简化处理，直接调用info
                taggedLogs.getClass().getMethod("info", String.class, Object[].class)
                    .invoke(taggedLogs, message, args);
                return;
            } catch (Exception e) {
                log.debug("[VRML] Failed to use vrml-log module with tags", e);
            }
        }
        
        // 降级到标准日志
        log.info("[{}][{}={}] {}", logKey, tagKey, tagValue, formatMessage(message, args));
    }
    
    /**
     * 检查vrml-log模块是否可用
     * 
     * @return true表示可用，false表示不可用
     */
    private static boolean isLogsAvailable() {
        if (logsAvailable == null) {
            synchronized (VrmlLogIntegration.class) {
                if (logsAvailable == null) {
                    try {
                        Class.forName("group.rxcloud.vrml.log.Logs");
                        logsAvailable = true;
                        log.debug("[VRML] vrml-log module is available");
                    } catch (ClassNotFoundException e) {
                        logsAvailable = false;
                        log.debug("[VRML] vrml-log module is not available");
                    }
                }
            }
        }
        return logsAvailable;
    }
    
    /**
     * 格式化消息
     * 
     * @param message 消息模板
     * @param args 参数
     * @return 格式化后的消息
     */
    private static String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return String.format(message.replace("{}", "%s"), args);
        } catch (Exception e) {
            return message + " " + java.util.Arrays.toString(args);
        }
    }
    
    /**
     * 检查是否启用日志
     * 
     * @return true表示启用，false表示禁用
     */
    public static boolean isLogEnabled() {
        return VrmlIntegrationManager.getConfig().isLoggingEnabled();
    }
}