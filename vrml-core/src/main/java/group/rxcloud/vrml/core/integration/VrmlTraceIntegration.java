package group.rxcloud.vrml.core.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * VRML链路追踪集成工具
 * 提供统一的链路追踪接口
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlTraceIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlTraceIntegration.class);
    
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String OPERATION_KEY = "operation";
    
    /**
     * 在追踪上下文中执行操作
     * 
     * @param traceKey 追踪键
     * @param operation 操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public static <T> T withTrace(String traceKey, Supplier<T> operation) {
        if (traceKey == null || traceKey.trim().isEmpty()) {
            return operation.get();
        }
        
        String traceId = generateTraceId();
        String spanId = generateSpanId();
        
        // 设置MDC
        String oldTraceId = MDC.get(TRACE_ID_KEY);
        String oldSpanId = MDC.get(SPAN_ID_KEY);
        String oldOperation = MDC.get(OPERATION_KEY);
        
        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(SPAN_ID_KEY, spanId);
            MDC.put(OPERATION_KEY, traceKey);
            
            // 尝试使用vrml-trace模块
            try {
                Class<?> tracesClass = Class.forName("group.rxcloud.vrml.trace.Traces");
                tracesClass.getMethod("tag", String.class, String.class).invoke(null, TRACE_ID_KEY, traceId);
                tracesClass.getMethod("tag", String.class, String.class).invoke(null, SPAN_ID_KEY, spanId);
                tracesClass.getMethod("tag", String.class, String.class).invoke(null, OPERATION_KEY, traceKey);
            } catch (Exception e) {
                // vrml-trace不可用，使用MDC即可
                log.debug("[VRML] vrml-trace module not available, using MDC only");
            }
            
            return operation.get();
            
        } finally {
            // 恢复MDC
            if (oldTraceId != null) {
                MDC.put(TRACE_ID_KEY, oldTraceId);
            } else {
                MDC.remove(TRACE_ID_KEY);
            }
            
            if (oldSpanId != null) {
                MDC.put(SPAN_ID_KEY, oldSpanId);
            } else {
                MDC.remove(SPAN_ID_KEY);
            }
            
            if (oldOperation != null) {
                MDC.put(OPERATION_KEY, oldOperation);
            } else {
                MDC.remove(OPERATION_KEY);
            }
        }
    }
    
    /**
     * 获取当前追踪ID
     * 
     * @return 追踪ID，如果不存在返回null
     */
    public static String getCurrentTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
    
    /**
     * 获取当前跨度ID
     * 
     * @return 跨度ID，如果不存在返回null
     */
    public static String getCurrentSpanId() {
        return MDC.get(SPAN_ID_KEY);
    }
    
    /**
     * 获取当前操作名称
     * 
     * @return 操作名称，如果不存在返回null
     */
    public static String getCurrentOperation() {
        return MDC.get(OPERATION_KEY);
    }
    
    /**
     * 设置追踪标签
     * 
     * @param key 标签键
     * @param value 标签值
     */
    public static void setTag(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        
        try {
            // 尝试使用vrml-trace模块
            Class<?> tracesClass = Class.forName("group.rxcloud.vrml.trace.Traces");
            tracesClass.getMethod("tag", String.class, String.class).invoke(null, key, value);
        } catch (Exception e) {
            // vrml-trace不可用，使用MDC
            MDC.put(key, value);
        }
    }
    
    /**
     * 移除追踪标签
     * 
     * @param key 标签键
     */
    public static void removeTag(String key) {
        if (key == null) {
            return;
        }
        
        try {
            // 尝试使用vrml-trace模块
            Class<?> tracesClass = Class.forName("group.rxcloud.vrml.trace.Traces");
            tracesClass.getMethod("remove", String.class).invoke(null, key);
        } catch (Exception e) {
            // vrml-trace不可用，使用MDC
            MDC.remove(key);
        }
    }
    
    /**
     * 生成追踪ID
     * 
     * @return 追踪ID
     */
    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成跨度ID
     * 
     * @return 跨度ID
     */
    private static String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 检查是否启用追踪
     * 
     * @return true表示启用，false表示禁用
     */
    public static boolean isTraceEnabled() {
        return VrmlIntegrationManager.getConfig().isTraceEnabled();
    }
}