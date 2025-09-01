package group.rxcloud.vrml.core.spi;

import group.rxcloud.vrml.core.api.VrmlConfiguration;
import group.rxcloud.vrml.core.api.VrmlOperations;
import io.vavr.control.Try;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * VrmlProviderRegistry测试类
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class VrmlProviderRegistryTest {
    
    @Before
    public void setUp() {
        VrmlProviderRegistry.clear();
    }
    
    @After
    public void tearDown() {
        VrmlProviderRegistry.clear();
    }
    
    @Test
    public void testRegisterProvider() {
        TestProvider provider = new TestProvider("test", "test-type", 50);
        VrmlProviderRegistry.registerProvider(TestProvider.class, provider);
        
        List<TestProvider> providers = VrmlProviderRegistry.getProviders(TestProvider.class);
        assertEquals(1, providers.size());
        assertEquals("test", providers.get(0).getName());
    }
    
    @Test
    public void testGetProvider() {
        TestProvider provider1 = new TestProvider("provider1", "type1", 100);
        TestProvider provider2 = new TestProvider("provider2", "type2", 50);
        
        VrmlProviderRegistry.registerProvider(TestProvider.class, provider1);
        VrmlProviderRegistry.registerProvider(TestProvider.class, provider2);
        
        TestProvider found = VrmlProviderRegistry.getProvider(TestProvider.class, "type1");
        assertNotNull(found);
        assertEquals("provider1", found.getName());
        
        found = VrmlProviderRegistry.getProvider(TestProvider.class, "type3");
        assertNull(found);
    }
    
    @Test
    public void testDefaultProvider() {
        TestProvider provider1 = new TestProvider("provider1", "type1", 100);
        TestProvider provider2 = new TestProvider("provider2", "type2", 50);
        
        VrmlProviderRegistry.registerProvider(TestProvider.class, provider1);
        VrmlProviderRegistry.registerProvider(TestProvider.class, provider2);
        
        // 默认应该返回优先级最高的（数值最小）
        TestProvider defaultProvider = VrmlProviderRegistry.getDefaultProvider(TestProvider.class);
        assertNotNull(defaultProvider);
        assertEquals("provider2", defaultProvider.getName());
        
        // 设置自定义默认提供者
        VrmlProviderRegistry.setDefaultProvider(TestProvider.class, provider1);
        defaultProvider = VrmlProviderRegistry.getDefaultProvider(TestProvider.class);
        assertEquals("provider1", defaultProvider.getName());
    }
    
    @Test
    public void testHealthyProviders() {
        TestProvider healthyProvider = new TestProvider("healthy", "type1", 100, true);
        TestProvider unhealthyProvider = new TestProvider("unhealthy", "type2", 50, false);
        
        VrmlProviderRegistry.registerProvider(TestProvider.class, healthyProvider);
        VrmlProviderRegistry.registerProvider(TestProvider.class, unhealthyProvider);
        
        List<TestProvider> healthyProviders = VrmlProviderRegistry.getHealthyProviders(TestProvider.class);
        assertEquals(1, healthyProviders.size());
        assertEquals("healthy", healthyProviders.get(0).getName());
    }
    
    // 测试用的Provider实现
    static class TestProvider implements VrmlProvider<TestOperations, TestConfiguration> {
        private final String name;
        private final String type;
        private final int priority;
        private final boolean healthy;
        
        public TestProvider(String name, String type, int priority) {
            this(name, type, priority, true);
        }
        
        public TestProvider(String name, String type, int priority, boolean healthy) {
            this.name = name;
            this.type = type;
            this.priority = priority;
            this.healthy = healthy;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getType() {
            return type;
        }
        
        @Override
        public TestOperations createOperations(String pattern, TestConfiguration config) {
            return new TestOperations();
        }
        
        @Override
        public boolean supports(String type) {
            return this.type.equals(type);
        }
        
        @Override
        public int getPriority() {
            return priority;
        }
        
        @Override
        public boolean isHealthy() {
            return healthy;
        }
    }
    
    // 测试用的Operations实现
    static class TestOperations implements VrmlOperations {
        @Override
        public VrmlOperations withMetrics(String metricName) {
            return this;
        }
        
        @Override
        public VrmlOperations withTrace(String traceKey) {
            return this;
        }
        
        @Override
        public VrmlOperations onError(Consumer<Throwable> errorHandler) {
            return this;
        }
        
        @Override
        public Try<Boolean> healthCheck() {
            return Try.success(true);
        }
    }
    
    // 测试用的Configuration实现
    static class TestConfiguration implements VrmlConfiguration {
        @Override
        public String getConfigPrefix() {
            return "test";
        }
    }
}