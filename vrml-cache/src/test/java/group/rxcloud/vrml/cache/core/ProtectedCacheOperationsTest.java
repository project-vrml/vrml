package group.rxcloud.vrml.cache.core;

import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.monitoring.CacheMonitor;
import group.rxcloud.vrml.cache.protection.CacheProtection;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 带防护和监控的缓存操作测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class ProtectedCacheOperationsTest {
    
    private CacheOperations delegate;
    private CacheProtection protection;
    private CacheMonitor monitor;
    private ProtectedCacheOperations protectedCache;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        delegate = mock(CacheOperations.class);
        protection = mock(CacheProtection.class);
        monitor = mock(CacheMonitor.class);
        
        protectedCache = new ProtectedCacheOperations(delegate, protection, monitor, "test-cache");
    }
    
    @Test
    void should_return_value_when_get_succeeds() {
        // Given
        String key = "user:123";
        String value = "test-value";
        when(delegate.get(key, String.class)).thenReturn(Try.success(Option.of(value)));
        
        // When
        Try<Option<String>> result = protectedCache.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isDefined());
        assertEquals(value, result.get().get());
        
        verify(monitor).recordOperation(eq("test-cache"), eq("get"), anyLong(), eq(true));
    }
    
    @Test
    public void testGetOrLoad_WithProtection() {
        // Given
        String key = "user:123";
        String value = "loaded-value";
        Supplier<String> loader = () -> value;
        Duration ttl = Duration.ofMinutes(30);
        
        when(protection.protectedGetOrLoad(delegate, key, String.class, loader, ttl))
            .thenReturn(Try.success(value));
        
        // When
        Try<String> result = protectedCache.getOrLoad(key, String.class, loader, ttl);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals(value, result.get());
        
        // 验证防护机制被调用
        verify(protection).protectedGetOrLoad(delegate, key, String.class, loader, ttl);
        verify(protection).recordKeyExists(key);
        
        // 验证监控记录
        verify(monitor).recordOperation(eq("test-cache"), eq("getOrLoad"), anyLong(), eq(true));
    }
    
    @Test
    public void testPut_WithAvalancheProtection() {
        // Given
        String key = "user:123";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(30);
        Duration protectedTtl = Duration.ofMinutes(32);
        
        when(protection.applyAvalancheProtection(ttl)).thenReturn(protectedTtl);
        when(delegate.put(key, value, protectedTtl)).thenReturn(Try.success(null));
        
        // When
        Try<Void> result = protectedCache.put(key, value, ttl);
        
        // Then
        assertTrue(result.isSuccess());
        
        // 验证雪崩防护被应用
        verify(protection).applyAvalancheProtection(ttl);
        verify(delegate).put(key, value, protectedTtl);
        verify(protection).recordKeyExists(key);
        
        // 验证监控记录
        verify(monitor).recordOperation(eq("test-cache"), eq("put"), anyLong(), eq(true));
    }
    
    @Test
    public void testEvict_WithMonitoring() {
        // Given
        String key = "user:123";
        when(delegate.evict(key)).thenReturn(Try.success(true));
        
        // When
        Try<Boolean> result = protectedCache.evict(key);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
        
        // 验证监控记录
        verify(monitor).recordOperation(eq("test-cache"), eq("evict"), anyLong(), eq(true));
    }
    
    @Test
    public void testHealthCheck_WithMonitoring() {
        // Given
        when(delegate.healthCheck()).thenReturn(Try.success(true));
        
        // When
        Try<Boolean> result = protectedCache.healthCheck();
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
        
        // 验证监控记录
        verify(monitor).recordOperation(eq("test-cache"), eq("healthCheck"), anyLong(), eq(true));
    }
    
    @Test
    public void should_handle_exception_when_get_fails() {
        // Given
        String key = "user:123";
        RuntimeException error = new RuntimeException("Cache error");
        when(delegate.get(key, String.class)).thenReturn(Try.failure(error));
        
        // When
        Try<Option<String>> result = protectedCache.get(key, String.class);
        
        // Then
        assertTrue(result.isFailure());
        assertEquals(error, result.getCause());
        
        verify(monitor).recordOperation(eq("test-cache"), eq("get"), anyLong(), eq(false));
    }
    
    @Test
    public void testCleanup() {
        // When
        protectedCache.cleanup();
        
        // Then
        verify(protection).cleanup();
        verify(monitor).unregisterCache("test-cache");
    }
    
    @Test
    public void testWithMetrics() {
        // When
        CacheOperations metricsOps = protectedCache.withMetrics("test.metrics");
        
        // Then
        assertNotNull(metricsOps);
        assertNotSame(protectedCache, metricsOps);
        assertTrue(metricsOps instanceof ProtectedCacheOperations);
    }
    
    @Test
    public void testWithTrace() {
        // When
        CacheOperations traceOps = protectedCache.withTrace("test.trace");
        
        // Then
        assertNotNull(traceOps);
        assertNotSame(protectedCache, traceOps);
        assertTrue(traceOps instanceof ProtectedCacheOperations);
    }
}