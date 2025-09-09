package group.rxcloud.vrml.cache.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.cache.core.AbstractCacheOperations;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis缓存操作实现
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class RedisCacheOperations extends AbstractCacheOperations {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // Redis Lua脚本用于原子操作
    private static final String GET_WITH_TTL_SCRIPT = 
        "local value = redis.call('GET', KEYS[1]) " +
        "if value then " +
        "  local ttl = redis.call('TTL', KEYS[1]) " +
        "  return {value, ttl} " +
        "else " +
        "  return nil " +
        "end";
    
    private static final String SET_IF_NOT_EXISTS_SCRIPT =
        "if redis.call('EXISTS', KEYS[1]) == 0 then " +
        "  redis.call('SETEX', KEYS[1], ARGV[2], ARGV[1]) " +
        "  return 1 " +
        "else " +
        "  return 0 " +
        "end";
    
    /**
     * 构造函数
     * 
     * @param pattern 缓存键模式
     * @param config 缓存配置
     * @param redisTemplate Redis模板
     */
    public RedisCacheOperations(String pattern, CacheConfiguration config, 
                               RedisTemplate<String, Object> redisTemplate) {
        super(pattern, config);
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 复制构造函数
     * 
     * @param other 其他实例
     */
    private RedisCacheOperations(RedisCacheOperations other) {
        super(other);
        this.redisTemplate = other.redisTemplate;
        this.objectMapper = other.objectMapper;
    }
    
    @Override
    protected AbstractCacheOperations createCopy() {
        return new RedisCacheOperations(this);
    }
    
    @Override
    protected <T> Try<Option<T>> doGet(String key, Class<T> valueType) {
        return Try.of(() -> {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Option.<T>none();
            }
            
            T deserializedValue = deserializeValue(value, valueType);
            return Option.<T>of(deserializedValue);
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to get cache value for key: {}", key, throwable);
            return Option.<T>none();
        });
    }
    
    @Override
    protected Try<Void> doPut(String key, Object value, Duration ttl) {
        return Try.run(() -> {
            Object serializedValue = serializeValue(value);
            if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
                redisTemplate.opsForValue().set(key, serializedValue, ttl.getSeconds(), TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, serializedValue);
            }
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to put cache value for key: {}", key, throwable);
            return null;
        });
    }
    
    @Override
    protected Try<Boolean> doEvict(String key) {
        return Try.of(() -> {
            Boolean result = redisTemplate.delete(key);
            return result != null && result;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to evict cache for key: {}", key, throwable);
            return false;
        });
    }
    
    @Override
    protected Try<Long> doEvictAll(List<String> keys) {
        return Try.of(() -> {
            if (keys.isEmpty()) {
                return 0L;
            }
            Long result = redisTemplate.delete(keys);
            return result != null ? result : 0L;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to evict cache for keys: {}", keys, throwable);
            return 0L;
        });
    }
    
    @Override
    protected Try<Long> doEvictByPattern(String keyPattern) {
        return Try.of(() -> {
            Set<String> keys = redisTemplate.keys(keyPattern);
            if (keys == null || keys.isEmpty()) {
                return 0L;
            }
            Long result = redisTemplate.delete(keys);
            return result != null ? result : 0L;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to evict cache by pattern: {}", keyPattern, throwable);
            return 0L;
        });
    }
    
    @Override
    protected Try<Boolean> doExists(String key) {
        return Try.of(() -> {
            Boolean result = redisTemplate.hasKey(key);
            return result != null && result;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to check cache existence for key: {}", key, throwable);
            return false;
        });
    }
    
    @Override
    protected Try<Boolean> doExpire(String key, Duration ttl) {
        return Try.of(() -> {
            Boolean result = redisTemplate.expire(key, ttl.getSeconds(), TimeUnit.SECONDS);
            return result != null && result;
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to set expiration for key: {}", key, throwable);
            return false;
        });
    }
    
    @Override
    protected Try<Duration> doGetTtl(String key) {
        return Try.of(() -> {
            Long ttlSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttlSeconds == null) {
                return Duration.ofSeconds(-2); // 键不存在
            }
            if (ttlSeconds == -1) {
                return Duration.ofSeconds(-1); // 永不过期
            }
            return Duration.ofSeconds(ttlSeconds);
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to get TTL for key: {}", key, throwable);
            return Duration.ofSeconds(-2);
        });
    }
    
    @Override
    protected <T> Try<Map<String, T>> doMultiGet(List<String> keys, Class<T> valueType) {
        return Try.of(() -> {
            if (keys.isEmpty()) {
                return new HashMap<>();
            }
            
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            Map<String, T> result = new HashMap<>();
            
            for (int i = 0; i < keys.size(); i++) {
                Object value = values != null && i < values.size() ? values.get(i) : null;
                if (value != null) {
                    T deserializedValue = deserializeValue(value, valueType);
                    result.put(keys.get(i), deserializedValue);
                }
            }
            
            return result;
        });
    }
    
    @Override
    protected Try<Void> doMultiPut(Map<String, Object> keyValues, Duration ttl) {
        return Try.run(() -> {
            if (keyValues.isEmpty()) {
                return;
            }
            
            // 序列化所有值
            Map<String, Object> serializedKeyValues = keyValues.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> serializeValue(entry.getValue())
                ));
            
            if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
                // 使用pipeline批量设置带TTL的缓存
                redisTemplate.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                    try {
                        serializedKeyValues.forEach((key, value) -> {
                            try {
                                byte[] keyBytes = key.getBytes();
                                byte[] valueBytes = objectMapper.writeValueAsBytes(value);
                                connection.setEx(keyBytes, ttl.getSeconds(), valueBytes);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to serialize value for key: " + key, e);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException("Pipeline operation failed", e);
                    }
                    return null;
                });
            } else {
                redisTemplate.opsForValue().multiSet(serializedKeyValues);
            }
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to multi-put cache values", throwable);
            return null;
        });
    }
    
    @Override
    protected Try<Void> doClear() {
        return Try.run(() -> {
            // 注意：这会清空整个Redis数据库，在生产环境中需要谨慎使用
            Set<String> keys = redisTemplate.keys(pattern + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to clear cache", throwable);
            return null;
        });
    }
    
    @Override
    protected Try<CacheStats> doGetStats() {
        return Try.of(() -> {
            // Redis本身不提供详细的统计信息，这里返回基本信息
            Set<String> keys = redisTemplate.keys(pattern + "*");
            long size = keys != null ? keys.size() : 0;
            
            return CacheStats.builder()
                .size(size)
                .hitCount(0) // Redis不跟踪命中次数
                .missCount(0)
                .loadCount(0)
                .loadExceptionCount(0)
                .totalLoadTime(0)
                .evictionCount(0)
                .build();
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Failed to get cache stats", throwable);
            return CacheStats.builder().build();
        });
    }
    
    @Override
    public Try<Boolean> healthCheck() {
        return Try.of(() -> {
            // 执行简单的ping命令检查Redis连接
            String result = redisTemplate.getConnectionFactory().getConnection().ping();
            return "PONG".equals(result);
        }).recover(throwable -> {
            log(LogLevel.ERROR, "Redis health check failed", throwable);
            return false;
        });
    }
    
    /**
     * 序列化值
     * 
     * @param value 原始值
     * @return 序列化后的值
     */
    private Object serializeValue(Object value) {
        if (value == null) {
            return null;
        }
        
        // 如果是基本类型，直接返回
        if (isPrimitiveType(value)) {
            return value;
        }
        
        // 复杂对象序列化为JSON字符串
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize value: " + value, e);
        }
    }
    
    /**
     * 反序列化值
     * 
     * @param value 序列化后的值
     * @param valueType 目标类型
     * @param <T> 目标类型
     * @return 反序列化后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T deserializeValue(Object value, Class<T> valueType) {
        if (value == null) {
            return null;
        }
        
        // 如果类型匹配，直接返回
        if (valueType.isInstance(value)) {
            return (T) value;
        }
        
        // 如果是字符串且目标类型是String，直接返回
        if (value instanceof String && valueType == String.class) {
            return (T) value;
        }
        
        // 如果是字符串，尝试JSON反序列化
        if (value instanceof String) {
            try {
                return objectMapper.readValue((String) value, valueType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize value: " + value + " to type: " + valueType, e);
            }
        }
        
        // 其他情况，尝试类型转换
        return valueType.cast(value);
    }
    
    /**
     * 检查是否为基本类型
     * 
     * @param value 值
     * @return 是否为基本类型
     */
    private boolean isPrimitiveType(Object value) {
        return value instanceof String ||
               value instanceof Number ||
               value instanceof Boolean ||
               value instanceof Character ||
               value.getClass().isPrimitive();
    }
}