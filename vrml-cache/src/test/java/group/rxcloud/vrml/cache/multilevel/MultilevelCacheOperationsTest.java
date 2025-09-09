package group.rxcloud.vrml.cache.multilevel;

import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.api.CacheStats;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 多级缓存操作测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class MultilevelCacheOperationsTest {
    
    private CacheOperations l1Cache;
    private CacheOperations l2Cache;
    private MultilevelCacheOperations multilevelCache;
    private MultilevelCacheConfiguration config;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        l1Cache = mock(CacheOperations.class);
        l2Cache = mock(CacheOperations.class);
        
        config = new MultilevelCacheConfiguration();
        config.setL1WriteBackEnabled(true);
        config.setWriteStrategy(MultilevelCacheConfiguration.WriteStrategy.WRITE_THROUGH);
        
        multilevelCache = new MultilevelCacheOperations("test:{}", config, l1Cache, l2Cache);
    }
    
    @Test
    public void testGet_L1Hit() {
        // Given
        String key = "user:123";
        String value = "test-value";
        when(l1Cache.get(key, String.class)).thenReturn(Try.success(Option.of(value)));
        
        // When
        Try<Option<String>> result = multilevelCache.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isDefined());
        assertEquals(value, result.get().get());
        
        // L1命中，不应该查询L2
        verify(l1Cache).get(key, String.class);
        verify(l2Cache, never()).get(any(), any());
    }
    
    @Test
    public void testGet_L1Miss_L2Hit() throws InterruptedException {
        // Given
        String key = "user:123";
        String value = "test-value";
        when(l1Cache.get(key, String.class)).thenReturn(Try.success(Option.none()));
        when(l2Cache.get(key, String.class)).thenReturn(Try.success(Option.of(value)));
        when(l1Cache.put(key, value)).thenReturn(Try.success(null));
        
        // When
        Try<Option<String>> result = multilevelCache.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isDefined());
        assertEquals(value, result.get().get());
        
        // 验证L1和L2都被查询
        verify(l1Cache).get(key, String.class);
        verify(l2Cache).get(key, String.class);
        
        // 等待异步回写完成
        Thread.sleep(100);
        verify(l1Cache).put(key, value);
    }
    
    @Test
    public void testGet_BothMiss() {
        // Given
        String key = "user:123";
        when(l1Cache.get(key, String.class)).thenReturn(Try.success(Option.none()));
        when(l2Cache.get(key, String.class)).thenReturn(Try.success(Option.none()));
        
        // When
        Try<Option<String>> result = multilevelCache.get(key, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isEmpty());
        
        verify(l1Cache).get(key, String.class);
        verify(l2Cache).get(key, String.class);
    }
    
    @Test
    public void testPut_WriteThrough() {
        // Given
        String key = "user:123";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(30);
        when(l1Cache.put(key, value, ttl)).thenReturn(Try.success(null));
        when(l2Cache.put(key, value, ttl)).thenReturn(Try.success(null));
        
        // When
        Try<Void> result = multilevelCache.put(key, value, ttl);
        
        // Then
        assertTrue(result.isSuccess());
        verify(l1Cache).put(key, value, ttl);
        verify(l2Cache).put(key, value, ttl);
    }
    
    @Test
    public void testPut_WriteBehind() {
        // Given
        config.setWriteStrategy(MultilevelCacheConfiguration.WriteStrategy.WRITE_BEHIND);
        multilevelCache = new MultilevelCacheOperations("test:{}", config, l1Cache, l2Cache);
        
        String key = "user:123";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(30);
        when(l1Cache.put(key, value, ttl)).thenReturn(Try.success(null));
        when(l2Cache.put(key, value, ttl)).thenReturn(Try.success(null));
        
        // When
        Try<Void> result = multilevelCache.put(key, value, ttl);
        
        // Then
        assertTrue(result.isSuccess());
        // 写回策略不等待完成，所以这里不验证具体调用
    }
    
    @Test
    public void testEvict_Success() {
        // Given
        String key = "user:123";
        when(l1Cache.evict(key)).thenReturn(Try.success(true));
        when(l2Cache.evict(key)).thenReturn(Try.success(true));
        
        // When
        Try<Boolean> result = multilevelCache.evict(key);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
        verify(l1Cache).evict(key);
        verify(l2Cache).evict(key);
    }
    
    @Test
    public void testExists_L1Exists() {
        // Given
        String key = "user:123";
        when(l1Cache.exists(key)).thenReturn(Try.success(true));
        
        // When
        Try<Boolean> result = multilevelCache.exists(key);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
        verify(l1Cache).exists(key);
        verify(l2Cache, never()).exists(any());
    }
    
    @Test
    public void testExists_L1NotExists_L2Exists() {
        // Given
        String key = "user:123";
        when(l1Cache.exists(key)).thenReturn(Try.success(false));
        when(l2Cache.exists(key)).thenReturn(Try.success(true));
        
        // When
        Try<Boolean> result = multilevelCache.exists(key);
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get());
        verify(l1Cache).exists(key);
        verify(l2Cache).exists(key);
    }
    
    @Test
    public void testMultiGet_PartialL1Hit() throws InterruptedException {
        // Given
        List<String> keys = Arrays.asList("user:123", "user:456");
        Map<String, String> l1Result = new HashMap<>();
        l1Result.put("test:user:123", "value1");
        
        Map<String, Object> l2Result = new HashMap<>();
        l2Result.put("test:user:456", "value2");
        
        when(l1Cache.multiGet(keys, String.class)).thenReturn(Try.success(l1Result));
        when(l2Cache.multiGet(Arrays.asList("user:456"), Object.class)).thenReturn(Try.success(l2Result));
        when(l1Cache.multiPut(l2Result)).thenReturn(Try.success(null));
        
        // When
        Try<Map<String, String>> result = multilevelCache.multiGet(keys, String.class);
        
        // Then
        assertTrue(result.isSuccess());
        Map<String, String> resultMap = result.get();
        assertEquals(2, resultMap.size());
        assertEquals("value1", resultMap.get("test:user:123"));
        assertEquals("value2", resultMap.get("test:user:456"));
        
        // 等待异步回写完成
        Thread.sleep(100);
        verify(l1Cache).multiPut(l2Result);
    }
    
    @Test
    public void testGetStats_Combined() {
        // Given
        CacheStats l1Stats = CacheStats.builder()
            .hitCount(10)
            .missCount(5)
            .size(100)
            .build();
        
        CacheStats l2Stats = CacheStats.builder()
            .hitCount(20)
            .missCount(10)
            .size(200)
            .build();
        
        when(l1Cache.getStats()).thenReturn(Try.success(l1Stats));
        when(l2Cache.getStats()).thenReturn(Try.success(l2Stats));
        
        // When
        Try<CacheStats> result = multilevelCache.getStats();
        
        // Then
        assertTrue(result.isSuccess());
        CacheStats combinedStats = result.get();
        assertEquals(30, combinedStats.getHitCount());
        assertEquals(15, combinedStats.getMissCount());
        assertEquals(300, combinedStats.getSize());
    }
    
    @Test
    public void testHealthCheck_StrictMode() {
        // Given
        config.setStrictHealthCheck(true);
        multilevelCache = new MultilevelCacheOperations("test:{}", config, l1Cache, l2Cache);
        
        when(l1Cache.healthCheck()).thenReturn(Try.success(true));
        when(l2Cache.healthCheck()).thenReturn(Try.success(false));
        
        // When
        Try<Boolean> result = multilevelCache.healthCheck();
        
        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.get()); // 严格模式下，两级都健康才算健康
    }
    
    @Test
    public void testHealthCheck_RelaxedMode() {
        // Given
        config.setStrictHealthCheck(false);
        multilevelCache = new MultilevelCacheOperations("test:{}", config, l1Cache, l2Cache);
        
        when(l1Cache.healthCheck()).thenReturn(Try.success(true));
        when(l2Cache.healthCheck()).thenReturn(Try.success(false));
        
        // When
        Try<Boolean> result = multilevelCache.healthCheck();
        
        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.get()); // 宽松模式下，任一级健康就算健康
    }
    
    @Test
    public void testClear_Success() {
        // Given
        when(l1Cache.clear()).thenReturn(Try.success(null));
        when(l2Cache.clear()).thenReturn(Try.success(null));
        
        // When
        Try<Void> result = multilevelCache.clear();
        
        // Then
        assertTrue(result.isSuccess());
        verify(l1Cache).clear();
        verify(l2Cache).clear();
    }
    
    @Test
    public void testShutdown() {
        // When
        multilevelCache.shutdown();
        
        // Then
        // 确保不抛异常即可
    }
}