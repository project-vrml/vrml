package group.rxcloud.vrml.cache.integration;

import group.rxcloud.vrml.cache.Caches;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * 缓存集成测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CacheIntegrationTest {
    
    private DefaultCacheConfiguration config;
    
    @Before
    public void setUp() {
        config = DefaultCacheConfiguration.builder()
            .cacheType("caffeine")
            .maxSize(1000)
            .defaultTtl(Duration.ofMinutes(10))
            .build();
    }
    
    @Test
    public void testLocalCacheBasicOperations() {
        // Given
        CacheOperations cache = Caches.local("test:{}", config);
        
        // When & Then - Put and Get
        Try<Void> putResult = cache.put("key1", "value1");
        assertTrue(putResult.isSuccess());
        
        Try<Option<String>> getResult = cache.get("key1", String.class);
        assertTrue(getResult.isSuccess());
        assertTrue(getResult.get().isDefined());
        assertEquals("value1", getResult.get().get());
        
        // When & Then - Exists
        Try<Boolean> existsResult = cache.exists("key1");
        assertTrue(existsResult.isSuccess());
        assertTrue(existsResult.get());
        
        // When & Then - Evict
        Try<Boolean> evictResult = cache.evict("key1");
        assertTrue(evictResult.isSuccess());
        
        Try<Option<String>> getAfterEvict = cache.get("key1", String.class);
        assertTrue(getAfterEvict.isSuccess());
        assertTrue(getAfterEvict.get().isEmpty());
    }
    
    @Test
    public void testGetOrLoad() {
        // Given
        CacheOperations cache = Caches.local("test:{}", config);
        
        // When
        Try<String> result = cache.getOrLoad("key2", String.class, 
            () -> "loaded-value", Duration.ofMinutes(5));
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals("loaded-value", result.get());
        
        // Verify it's cached
        Try<Option<String>> cachedResult = cache.get("key2", String.class);
        assertTrue(cachedResult.isSuccess());
        assertTrue(cachedResult.get().isDefined());
        assertEquals("loaded-value", cachedResult.get().get());
    }
    
    @Test
    public void testWithMetricsAndTrace() {
        // Given
        CacheOperations cache = Caches.local("test:{}", config)
            .withMetrics("test.cache")
            .withTrace("cache.test");
        
        // When & Then
        Try<Void> putResult = cache.put("key3", "value3");
        assertTrue(putResult.isSuccess());
        
        Try<Option<String>> getResult = cache.get("key3", String.class);
        assertTrue(getResult.isSuccess());
        assertTrue(getResult.get().isDefined());
        assertEquals("value3", getResult.get().get());
    }
    
    @Test
    public void testHealthCheck() {
        // Given
        CacheOperations cache = Caches.local("test:{}", config);
        
        // When
        Try<Boolean> healthResult = cache.healthCheck();
        
        // Then
        assertTrue(healthResult.isSuccess());
        assertTrue(healthResult.get());
    }
    
    @Test
    public void testCacheStats() {
        // Given
        CacheOperations cache = Caches.local("test:{}", config);
        
        // Perform some operations
        cache.put("key4", "value4");
        cache.get("key4", String.class); // Hit
        cache.get("nonexistent", String.class); // Miss
        
        // When
        Try<group.rxcloud.vrml.cache.api.CacheStats> statsResult = cache.getStats();
        
        // Then
        assertTrue(statsResult.isSuccess());
        group.rxcloud.vrml.cache.api.CacheStats stats = statsResult.get();
        assertNotNull(stats);
        assertTrue(stats.getSize() >= 0);
    }
    
    @Test
    public void testErrorHandling() {
        // Given
        CacheOperations cache = Caches.local("test:{}", config)
            .onError(error -> System.err.println("Cache error: " + error.getMessage()));
        
        // When - Test with null key (should handle gracefully)
        Try<Option<String>> result = cache.get(null, String.class);
        
        // Then - Should not throw exception
        assertNotNull(result);
    }
}