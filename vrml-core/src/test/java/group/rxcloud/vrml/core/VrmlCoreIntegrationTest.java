package group.rxcloud.vrml.core;

import group.rxcloud.vrml.core.api.AbstractVrmlOperations;
import group.rxcloud.vrml.core.api.VrmlConfigurationManager;
import group.rxcloud.vrml.core.integration.VrmlIntegrationManager;
import group.rxcloud.vrml.core.spi.VrmlProviderRegistry;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * VRML核心模块集成测试
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class VrmlCoreIntegrationTest {
    
    @Before
    public void setUp() {
        VrmlProviderRegistry.clear();
        VrmlConfigurationManager.clearCache();
    }
    
    @Test
    public void testIntegrationManagerInitialization() {
        // 测试集成管理器初始化
        VrmlIntegrationManager.initialize();
        
        VrmlIntegrationManager.IntegrationConfig config = VrmlIntegrationManager.getConfig();
        assertNotNull(config);
        
        // 由于测试环境中没有相关模块，这些应该都是false
        assertFalse(config.isMetricsEnabled());
        assertFalse(config.isLoggingEnabled());
        assertFalse(config.isAlertEnabled());
        assertFalse(config.isTraceEnabled());
    }
    
    @Test
    public void testAbstractOperationsWithoutIntegration() {
        TestOperations ops = new TestOperations();
        
        Try<String> result = ops.testOperation("input");
        assertTrue(result.isSuccess());
        assertEquals("processed: input", result.get());
    }
    
    @Test
    public void testAbstractOperationsWithConfiguration() {
        TestOperations ops = new TestOperations()
            .withMetrics("test.metric")
            .withTrace("test.trace");
        
        assertNotNull(ops);
        
        Try<String> result = ops.testOperation("input");
        assertTrue(result.isSuccess());
        assertEquals("processed: input", result.get());
    }
    
    @Test
    public void testConfigurationManager() {
        // 测试系统属性配置
        System.setProperty("test.config", "test.value");
        
        String value = VrmlConfigurationManager.getString("test.config", "default");
        assertEquals("test.value", value);
        
        // 测试缓存
        assertEquals(1, VrmlConfigurationManager.getCacheSize());
        
        // 测试配置存在检查
        assertTrue(VrmlConfigurationManager.hasConfig("test.config"));
        assertFalse(VrmlConfigurationManager.hasConfig("nonexistent.config"));
        
        System.clearProperty("test.config");
    }
    
    @Test
    public void testProviderRegistry() {
        assertEquals(0, VrmlProviderRegistry.getProviders(TestProvider.class).size());
        
        TestProvider provider = new TestProvider();
        VrmlProviderRegistry.registerProvider(TestProvider.class, provider);
        
        assertEquals(1, VrmlProviderRegistry.getProviders(TestProvider.class).size());
        
        TestProvider found = VrmlProviderRegistry.getProvider(TestProvider.class, "test");
        assertNotNull(found);
        assertEquals("test-provider", found.getName());
    }
    
    // 测试用的操作类
    static class TestOperations extends AbstractVrmlOperations {
        
        public TestOperations() {
            super();
        }
        
        public TestOperations(TestOperations other) {
            super(other);
        }
        
        @Override
        protected AbstractVrmlOperations createCopy() {
            return new TestOperations(this);
        }
        
        public Try<String> testOperation(String input) {
            return executeWithIntegration("testOperation", () -> {
                return Try.success("processed: " + input);
            });
        }
    }
    
    // 测试用的提供者类
    static class TestProvider implements group.rxcloud.vrml.core.spi.VrmlProvider<TestOperations, TestConfiguration> {
        
        @Override
        public String getName() {
            return "test-provider";
        }
        
        @Override
        public String getType() {
            return "test";
        }
        
        @Override
        public TestOperations createOperations(String pattern, TestConfiguration config) {
            return new TestOperations();
        }
        
        @Override
        public boolean supports(String type) {
            return "test".equals(type);
        }
    }
    
    // 测试用的配置类
    static class TestConfiguration implements group.rxcloud.vrml.core.api.VrmlConfiguration {
        
        @Override
        public String getConfigPrefix() {
            return "test";
        }
    }
}