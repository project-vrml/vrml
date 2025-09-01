package group.rxcloud.vrml.core.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * VrmlConfigurationManager测试类
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class VrmlConfigurationManagerTest {
    
    @Before
    public void setUp() {
        VrmlConfigurationManager.clearCache();
    }
    
    @After
    public void tearDown() {
        VrmlConfigurationManager.clearCache();
        VrmlConfigurationManager.setConfigProvider(null);
    }
    
    @Test
    public void testGetString() {
        // 测试默认值
        String result = VrmlConfigurationManager.getString("test.key", "default");
        assertEquals("default", result);
        
        // 测试系统属性
        System.setProperty("test.key", "system.value");
        result = VrmlConfigurationManager.getString("test.key", "default");
        assertEquals("system.value", result);
        
        System.clearProperty("test.key");
    }
    
    @Test
    public void testGetInt() {
        // 测试默认值
        int result = VrmlConfigurationManager.getInt("test.int", 100);
        assertEquals(100, result);
        
        // 测试有效值
        System.setProperty("test.int", "200");
        result = VrmlConfigurationManager.getInt("test.int", 100);
        assertEquals(200, result);
        
        // 测试无效值
        System.setProperty("test.int", "invalid");
        result = VrmlConfigurationManager.getInt("test.int", 100);
        assertEquals(100, result);
        
        System.clearProperty("test.int");
    }
    
    @Test
    public void testGetBoolean() {
        // 测试默认值
        boolean result = VrmlConfigurationManager.getBoolean("test.bool", false);
        assertFalse(result);
        
        // 测试true值
        System.setProperty("test.bool", "true");
        result = VrmlConfigurationManager.getBoolean("test.bool", false);
        assertTrue(result);
        
        // 测试1值
        System.setProperty("test.bool", "1");
        result = VrmlConfigurationManager.getBoolean("test.bool", false);
        assertTrue(result);
        
        // 测试false值
        System.setProperty("test.bool", "false");
        result = VrmlConfigurationManager.getBoolean("test.bool", true);
        assertFalse(result);
        
        System.clearProperty("test.bool");
    }
    
    @Test
    public void testConfigProvider() {
        // 设置配置提供者
        VrmlConfigurationManager.setConfigProvider(key -> {
            if ("provider.key".equals(key)) {
                return "provider.value";
            }
            return null;
        });
        
        String result = VrmlConfigurationManager.getString("provider.key", "default");
        assertEquals("provider.value", result);
        
        result = VrmlConfigurationManager.getString("unknown.key", "default");
        assertEquals("default", result);
    }
    
    @Test
    public void testHasConfig() {
        // 测试不存在的配置
        assertFalse(VrmlConfigurationManager.hasConfig("nonexistent.key"));
        
        // 测试系统属性
        System.setProperty("exists.key", "value");
        assertTrue(VrmlConfigurationManager.hasConfig("exists.key"));
        
        System.clearProperty("exists.key");
    }
    
    @Test
    public void testCache() {
        assertEquals(0, VrmlConfigurationManager.getCacheSize());
        
        VrmlConfigurationManager.getString("cache.key", "default");
        assertEquals(1, VrmlConfigurationManager.getCacheSize());
        
        VrmlConfigurationManager.clearCache();
        assertEquals(0, VrmlConfigurationManager.getCacheSize());
    }
}