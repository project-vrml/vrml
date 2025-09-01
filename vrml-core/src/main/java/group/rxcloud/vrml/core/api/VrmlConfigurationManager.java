package group.rxcloud.vrml.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * VRML配置管理器
 * 提供统一的配置管理功能
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public final class VrmlConfigurationManager {
    
    private static final Logger log = LoggerFactory.getLogger(VrmlConfigurationManager.class);
    
    /**
     * 配置缓存
     */
    private static final Map<String, Object> CONFIG_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 配置提供者
     */
    private static volatile Function<String, String> configProvider;
    
    /**
     * 设置配置提供者
     * 
     * @param provider 配置提供者
     */
    public static void setConfigProvider(Function<String, String> provider) {
        configProvider = provider;
        log.info("[VRML] Configuration provider set");
    }
    
    /**
     * 获取字符串配置
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getString(String key, String defaultValue) {
        return getConfig(key, defaultValue, String::valueOf);
    }
    
    /**
     * 获取整数配置
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static int getInt(String key, int defaultValue) {
        return getConfig(key, defaultValue, value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("[VRML] Invalid integer config for key: {}, value: {}, using default: {}", 
                    key, value, defaultValue);
                return defaultValue;
            }
        });
    }
    
    /**
     * 获取长整数配置
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static long getLong(String key, long defaultValue) {
        return getConfig(key, defaultValue, value -> {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.warn("[VRML] Invalid long config for key: {}, value: {}, using default: {}", 
                    key, value, defaultValue);
                return defaultValue;
            }
        });
    }
    
    /**
     * 获取布尔配置
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return getConfig(key, defaultValue, value -> {
            if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
                return true;
            } else if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
                return false;
            } else {
                log.warn("[VRML] Invalid boolean config for key: {}, value: {}, using default: {}", 
                    key, value, defaultValue);
                return defaultValue;
            }
        });
    }
    
    /**
     * 获取双精度浮点数配置
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static double getDouble(String key, double defaultValue) {
        return getConfig(key, defaultValue, value -> {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                log.warn("[VRML] Invalid double config for key: {}, value: {}, using default: {}", 
                    key, value, defaultValue);
                return defaultValue;
            }
        });
    }
    
    /**
     * 获取配置值
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @param converter 转换器
     * @param <T> 配置值类型
     * @return 配置值
     */
    @SuppressWarnings("unchecked")
    private static <T> T getConfig(String key, T defaultValue, Function<String, T> converter) {
        if (key == null || key.trim().isEmpty()) {
            return defaultValue;
        }
        
        // 先从缓存获取
        Object cached = CONFIG_CACHE.get(key);
        if (cached != null) {
            try {
                return (T) cached;
            } catch (ClassCastException e) {
                log.warn("[VRML] Config cache type mismatch for key: {}", key);
                CONFIG_CACHE.remove(key);
            }
        }
        
        // 从配置提供者获取
        String value = null;
        if (configProvider != null) {
            try {
                value = configProvider.apply(key);
            } catch (Exception e) {
                log.warn("[VRML] Failed to get config for key: {}", key, e);
            }
        }
        
        // 如果没有配置提供者或获取失败，尝试从系统属性获取
        if (value == null) {
            value = System.getProperty(key);
        }
        
        // 如果还是没有，尝试从环境变量获取
        if (value == null) {
            value = System.getenv(key);
        }
        
        // 转换并缓存
        T result;
        if (value != null) {
            result = converter.apply(value);
        } else {
            result = defaultValue;
        }
        
        CONFIG_CACHE.put(key, result);
        return result;
    }
    
    /**
     * 清空配置缓存
     */
    public static void clearCache() {
        CONFIG_CACHE.clear();
        log.info("[VRML] Configuration cache cleared");
    }
    
    /**
     * 获取配置缓存大小
     * 
     * @return 缓存大小
     */
    public static int getCacheSize() {
        return CONFIG_CACHE.size();
    }
    
    /**
     * 检查配置是否存在
     * 
     * @param key 配置键
     * @return true表示存在，false表示不存在
     */
    public static boolean hasConfig(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // 检查缓存
        if (CONFIG_CACHE.containsKey(key)) {
            return true;
        }
        
        // 检查配置提供者
        if (configProvider != null) {
            try {
                String value = configProvider.apply(key);
                if (value != null) {
                    return true;
                }
            } catch (Exception e) {
                log.debug("[VRML] Failed to check config for key: {}", key, e);
            }
        }
        
        // 检查系统属性
        if (System.getProperty(key) != null) {
            return true;
        }
        
        // 检查环境变量
        return System.getenv(key) != null;
    }
}