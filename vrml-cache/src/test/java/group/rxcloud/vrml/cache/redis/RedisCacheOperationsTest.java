package group.rxcloud.vrml.cache.redis;

import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Redis缓存操作测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class RedisCacheOperationsTest {
    
    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private RedisCacheOperations cacheOperations;
    private DefaultCacheConfiguration config;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        config = new DefaultCacheConfiguration();
        cacheOperations = new RedisCacheOperations("test:{}", config, redisTemplate);
    }
    
    @Test
    public void testGet_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        when(valueOperations.get("test:user:123")).thenReturn(value);
        
        // When
        Try<Option<String>> result = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isDefined());
        assertEquals(value, result.get().get());
    }
    
    @Test
    public void testGet_NotFound() {
        // Given
        String key = "user:123";
        when(valueOperations.get("test:user:123")).thenReturn(null);
        
        // When
        Try<Option<String>> result = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isEmpty());
    }
    
    @Test
    public void testPut_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        
        // When
        Try<Void> result = cacheOperations.put(key, value);
        
        // Then
        assertTrue(result.isSuccess());
        verify(valueOperations).set("test:user:123", value, config.getDefaultTtl().getSeconds(), 
            java.util.concurrent.TimeUnit.SECONDS);
    }
    
    @Test
    public void testPutWithTtl_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(30);
        
        // When
        Try<Void> result = cacheOperations.put(key, value, ttl);
        
        // Then
        assertTrue(result.isSuccess());
        verify(valueOperations).set("test:user:123", value, ttl.getSeconds(), 
            java.util.concurrent.TimeUnit.SECONDS);
    }
    
    @Test
    public void testEvict_Success() {
        // Given
        String key = "user:123";
        when(redisTemplate.delete("test:user:123")).thenReturn(true);
        
        // When
        Try<Boolean> result = cacheOperations.evict(key);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
    }
    
    @Test
    public void testExists_Success() {
        // Given
        String key = "user:123";
        when(redisTemplate.hasKey("test:user:123")).thenReturn(true);
        
        // When
        Try<Boolean> result = cacheOperations.exists(key);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
    }
    
    @Test
    public void testMultiGet_Success() {
        // Given
        List<String> keys = Arrays.asList("user:123", "user:456");
        List<Object> values = Arrays.asList("value1", "value2");
        when(valueOperations.multiGet(Arrays.asList("test:user:123", "test:user:456")))
            .thenReturn(values);
        
        // When
        Try<Map<String, String>> result = cacheOperations.multiGet(keys, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        Map<String, String> resultMap = result.get();
        assertEquals(2, resultMap.size());
        assertEquals("value1", resultMap.get("test:user:123"));
        assertEquals("value2", resultMap.get("test:user:456"));
    }
    
    @Test
    public void testMultiPut_Success() {
        // Given
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put("user:123", "value1");
        keyValues.put("user:456", "value2");
        
        // When
        Try<Void> result = cacheOperations.multiPut(keyValues);
        
        // Then
        assertTrue(result.isSuccess());
        // 验证pipeline操作被调用
        verify(redisTemplate).executePipelined(any(org.springframework.data.redis.core.RedisCallback.class));
    }
    
    @Test
    public void testGetStats_Success() {
        // Given
        when(redisTemplate.keys("test:{}*")).thenReturn(java.util.Set.of("key1", "key2"));
        
        // When
        Try<CacheStats> result = cacheOperations.getStats();
        
        // Then
        assertTrue(result.isSuccess());
        CacheStats stats = result.get();
        assertEquals(2, stats.getSize());
    }
    
    @Test
    public void testHealthCheck_Success() {
        // Given
        org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory = 
            mock(org.springframework.data.redis.connection.RedisConnectionFactory.class);
        org.springframework.data.redis.connection.RedisConnection connection = 
            mock(org.springframework.data.redis.connection.RedisConnection.class);
        
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("PONG");
        
        // When
        Try<Boolean> result = cacheOperations.healthCheck();
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
    }
    
    @Test
    public void testWithMetrics() {
        // When
        RedisCacheOperations metricsOps = (RedisCacheOperations) cacheOperations.withMetrics("test.cache");
        
        // Then
        assertNotNull(metricsOps);
        assertNotSame(cacheOperations, metricsOps);
    }
    
    @Test
    public void testWithTrace() {
        // When
        RedisCacheOperations traceOps = (RedisCacheOperations) cacheOperations.withTrace("cache.trace");
        
        // Then
        assertNotNull(traceOps);
        assertNotSame(cacheOperations, traceOps);
    }
}