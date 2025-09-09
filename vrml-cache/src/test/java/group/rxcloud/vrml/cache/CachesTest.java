package group.rxcloud.vrml.cache;

import group.rxcloud.vrml.cache.api.CacheConfiguration;
import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Caches类的单元测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CachesTest {
    
    @Test
    public void testFormatKey() {
        // 测试键格式化功能
        String pattern = "user:{}:profile";
        String result = Caches.formatKey(pattern, "123");
        assertEquals("user:123:profile", result);
        
        // 测试多个占位符
        String multiPattern = "user:{}:session:{}";
        String multiResult = Caches.formatKey(multiPattern, "123", "abc");
        assertEquals("user:123:session:abc", multiResult);
        
        // 测试无占位符
        String noPattern = "simple-key";
        String noResult = Caches.formatKey(noPattern);
        assertEquals("simple-key", noResult);
    }
    
    @Test
    public void testDefaultConfiguration() {
        CacheConfiguration config = Caches.getDefaultConfiguration();
        assertNotNull(config);
        assertTrue(config.isEnabled());
        assertTrue(config.isMetricsEnabled());
        assertTrue(config.isTraceEnabled());
    }
    
    @Test
    public void testSetDefaultConfiguration() {
        CacheConfiguration originalConfig = Caches.getDefaultConfiguration();
        
        CacheConfiguration newConfig = DefaultCacheConfiguration.builder()
            .cacheType("redis")
            .maxSize(5000L)
            .build();
        
        Caches.setDefaultConfiguration(newConfig);
        assertEquals(newConfig, Caches.getDefaultConfiguration());
        
        // 恢复原配置
        Caches.setDefaultConfiguration(originalConfig);
    }
    
    @Test
    public void testClearInstances() {
        // 测试清空实例缓存
        Caches.clearInstances();
        // 这个方法主要用于测试，确保不抛异常即可
    }
}