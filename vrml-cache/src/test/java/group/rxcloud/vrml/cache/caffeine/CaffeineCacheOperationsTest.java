package group.rxcloud.vrml.cache.caffeine;

import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Caffeine缓存操作测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CaffeineCacheOperationsTest {
    
    private CaffeineCacheOperations cacheOperations;
    private DefaultCacheConfiguration config;
    
    @Before
    public void setUp() {
        config = new DefaultCacheConfiguration();
        config.setMaxSize(1000);
        config.setMetricsEnabled(true);
        cacheOperations = new CaffeineCacheOperations("test:{}", config);
    }
    
    @Test
    public void testPutAndGet_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        
        // When
        Try<Void> putResult = cacheOperations.put(key, value);
        Try<Option<String>> getResult = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(putResult.isSuccess());
        assertTrue(getResult.isSuccess());
        assertTrue(getResult.get().isDefined());
        assertEquals(value, getResult.get().get());
    }
    
    @Test
    public void testGet_NotFound() {
        // Given
        String key = "nonexistent:123";
        
        // When
        Try<Option<String>> result = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isEmpty());
    }
    
    @Test
    public void testPutWithTtl_Success() throws InterruptedException {
        // Given
        String key = "user:123";
        String value = "test-value";
        Duration ttl = Duration.ofMillis(100);
        
        // When
        Try<Void> putResult = cacheOperations.put(key, value, ttl);
        Try<Option<String>> getResult1 = cacheOperations.get(key, String.class);
        
        // Wait for expiration
        Thread.sleep(150);
        Try<Option<String>> getResult2 = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(putResult.isSuccess());
        assertTrue(getResult1.isSuccess());
        assertTrue(getResult1.get().isDefined());
        assertEquals(value, getResult1.get().get());
        
        assertTrue(getResult2.isSuccess());
        assertTrue(getResult2.get().isEmpty()); // Should be expired
    }
    
    @Test
    public void testEvict_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        cacheOperations.put(key, value);
        
        // When
        Try<Boolean> evictResult = cacheOperations.evict(key);
        Try<Option<String>> getResult = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(evictResult.isSuccess());
        assertTrue(evictResult.get());
        assertTrue(getResult.isSuccess());
        assertTrue(getResult.get().isEmpty());
    }
    
    @Test
    public void testExists_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        cacheOperations.put(key, value);
        
        // When
        Try<Boolean> existsResult1 = cacheOperations.exists(key);
        Try<Boolean> existsResult2 = cacheOperations.exists("nonexistent:456");
        
        // Then
        assertTrue(existsResult1.isSuccess());
        assertTrue(existsResult1.get());
        assertTrue(existsResult2.isSuccess());
        assertFalse(existsResult2.get());
    }
    
    @Test
    public void testGetTtl_Success() {
        // Given
        String key = "user:123";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(10);
        cacheOperations.put(key, value, ttl);
        
        // When
        Try<Duration> ttlResult = cacheOperations.getTtl(key);
        
        // Then
        assertTrue(ttlResult.isSuccess());
        Duration remainingTtl = ttlResult.get();
        assertTrue(remainingTtl.getSeconds() > 0);
        assertTrue(remainingTtl.getSeconds() <= ttl.getSeconds());
    }
    
    @Test
    public void testMultiGet_Success() {
        // Given
        cacheOperations.put("user:123", "value1");
        cacheOperations.put("user:456", "value2");
        List<String> keys = Arrays.asList("user:123", "user:456", "user:789");
        
        // When
        Try<Map<String, String>> result = cacheOperations.multiGet(keys, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        Map<String, String> resultMap = result.get();
        assertEquals(2, resultMap.size());
        assertEquals("value1", resultMap.get("test:user:123"));
        assertEquals("value2", resultMap.get("test:user:456"));
        assertFalse(resultMap.containsKey("test:user:789"));
    }
    
    @Test
    public void testMultiPut_Success() {
        // Given
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put("user:123", "value1");
        keyValues.put("user:456", "value2");
        
        // When
        Try<Void> putResult = cacheOperations.multiPut(keyValues);
        Try<Option<String>> getResult1 = cacheOperations.get("user:123", String.class);
        Try<Option<String>> getResult2 = cacheOperations.get("user:456", String.class);
        
        // Then
        assertTrue(putResult.isSuccess());
        assertTrue(getResult1.isSuccess());
        assertTrue(getResult1.get().isDefined());
        assertEquals("value1", getResult1.get().get());
        assertTrue(getResult2.isSuccess());
        assertTrue(getResult2.get().isDefined());
        assertEquals("value2", getResult2.get().get());
    }
    
    @Test
    public void testEvictAll_Success() {
        // Given
        cacheOperations.put("user:123", "value1");
        cacheOperations.put("user:456", "value2");
        cacheOperations.put("user:789", "value3");
        List<String> keysToEvict = Arrays.asList("user:123", "user:456");
        
        // When
        Try<Long> evictResult = cacheOperations.evictAll(keysToEvict);
        
        // Then
        assertTrue(evictResult.isSuccess());
        assertEquals(2L, evictResult.get().longValue());
        
        // Verify eviction
        assertTrue(cacheOperations.get("user:123", String.class).get().isEmpty());
        assertTrue(cacheOperations.get("user:456", String.class).get().isEmpty());
        assertTrue(cacheOperations.get("user:789", String.class).get().isDefined());
    }
    
    @Test
    public void testEvictByPattern_Success() {
        // Given
        cacheOperations.put("user:123", "value1");
        cacheOperations.put("user:456", "value2");
        cacheOperations.put("order:789", "value3");
        
        // When
        Try<Long> evictResult = cacheOperations.evictByPattern("test:user:*");
        
        // Then
        assertTrue(evictResult.isSuccess());
        assertEquals(2L, evictResult.get().longValue());
        
        // Verify eviction
        assertTrue(cacheOperations.get("user:123", String.class).get().isEmpty());
        assertTrue(cacheOperations.get("user:456", String.class).get().isEmpty());
        assertTrue(cacheOperations.get("order:789", String.class).get().isDefined());
    }
    
    @Test
    public void testClear_Success() {
        // Given
        cacheOperations.put("user:123", "value1");
        cacheOperations.put("user:456", "value2");
        
        // When
        Try<Void> clearResult = cacheOperations.clear();
        
        // Then
        assertTrue(clearResult.isSuccess());
        assertTrue(cacheOperations.get("user:123", String.class).get().isEmpty());
        assertTrue(cacheOperations.get("user:456", String.class).get().isEmpty());
    }
    
    @Test
    public void testGetStats_Success() {
        // Given
        cacheOperations.put("user:123", "value1");
        cacheOperations.get("user:123", String.class); // Hit
        cacheOperations.get("user:456", String.class); // Miss
        
        // When
        Try<CacheStats> statsResult = cacheOperations.getStats();
        
        // Then
        assertTrue(statsResult.isSuccess());
        CacheStats stats = statsResult.get();
        assertTrue(stats.getSize() >= 0);
        assertTrue(stats.getHitCount() >= 0);
        assertTrue(stats.getMissCount() >= 0);
    }
    
    @Test
    public void testHealthCheck_Success() {
        // When
        Try<Boolean> healthResult = cacheOperations.healthCheck();
        
        // Then
        assertTrue(healthResult.isSuccess());
        assertTrue(healthResult.get());
    }
    
    @Test
    public void testWithMetrics() {
        // When
        CaffeineCacheOperations metricsOps = (CaffeineCacheOperations) cacheOperations.withMetrics("test.cache");
        
        // Then
        assertNotNull(metricsOps);
        assertNotSame(cacheOperations, metricsOps);
    }
    
    @Test
    public void testWithTrace() {
        // When
        CaffeineCacheOperations traceOps = (CaffeineCacheOperations) cacheOperations.withTrace("cache.trace");
        
        // Then
        assertNotNull(traceOps);
        assertNotSame(cacheOperations, traceOps);
    }
    
    @Test
    public void testTypeConversion() {
        // Given
        String key = "number:123";
        Integer value = 42;
        
        // When
        cacheOperations.put(key, value);
        Try<Option<Integer>> intResult = cacheOperations.get(key, Integer.class);
        Try<Option<String>> stringResult = cacheOperations.get(key, String.class);
        
        // Then
        assertTrue(intResult.isSuccess());
        assertTrue(intResult.get().isDefined());
        assertEquals(value, intResult.get().get());
        
        assertTrue(stringResult.isSuccess());
        assertTrue(stringResult.get().isDefined());
        assertEquals("42", stringResult.get().get());
    }
}