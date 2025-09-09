package group.rxcloud.vrml.cache.examples;

import group.rxcloud.vrml.cache.Caches;
import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.caffeine.CaffeineCacheConfiguration;
import group.rxcloud.vrml.cache.config.DefaultCacheConfiguration;
import group.rxcloud.vrml.cache.multilevel.MultilevelCacheConfiguration;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存使用示例
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CacheExamples {
    
    /**
     * 基本缓存操作示例
     */
    public static void basicCacheOperations() {
        // 创建本地缓存
        CacheOperations cache = Caches.local("user:{}");
        
        // 存储数据
        Try<Void> putResult = cache.put("123", createUser("123", "Alice"), Duration.ofMinutes(30));
        if (putResult.isSuccess()) {
            System.out.println("User cached successfully");
        }
        
        // 获取数据
        Try<Option<User>> getResult = cache.get("123", User.class);
        if (getResult.isSuccess() && getResult.get().isDefined()) {
            User user = getResult.get().get();
            System.out.println("Found user: " + user.getName());
        } else {
            System.out.println("User not found in cache");
        }
        
        // 删除数据
        Try<Boolean> evictResult = cache.evict("123");
        if (evictResult.isSuccess() && evictResult.get()) {
            System.out.println("User evicted from cache");
        }
    }
    
    /**
     * 缓存穿透防护示例
     */
    public static void cacheWithProtection() {
        // 配置防护机制
        DefaultCacheConfiguration config = DefaultCacheConfiguration.builder()
            .cacheType("caffeine")
            .maxSize(10000)
            .defaultTtl(Duration.ofHours(1))
            .bloomFilterEnabled(true)
            .hotKeyProtectionEnabled(true)
            .avalancheProtectionEnabled(true)
            .build();
        
        CacheOperations cache = Caches.local("product:{}", config);
        
        // 使用getOrLoad自动处理缓存未命中
        Try<Product> productResult = cache.getOrLoad("456", Product.class, 
            () -> loadProductFromDatabase("456"), Duration.ofMinutes(30));
        
        if (productResult.isSuccess()) {
            Product product = productResult.get();
            if (product != null) {
                System.out.println("Product loaded: " + product.getName());
            } else {
                System.out.println("Product not found");
            }
        }
    }
    
    /**
     * 多级缓存示例
     */
    public static void multilevelCacheExample() {
        // 配置多级缓存
        MultilevelCacheConfiguration config = MultilevelCacheConfiguration.multilevelBuilder()
            .l1CacheType("caffeine")
            .l2CacheType("redis")
            .l1MaxSize(1000)
            .l2MaxSize(10000)
            .l1DefaultTtl(Duration.ofMinutes(10))
            .l2DefaultTtl(Duration.ofHours(1))
            .writeStrategy(MultilevelCacheConfiguration.WriteStrategy.WRITE_THROUGH)
            .l1WriteBackEnabled(true)
            .build();
        
        CacheOperations cache = Caches.multilevel("session:{}", config);
        
        // 存储会话数据
        Session session = createSession("session123", "user456");
        Try<Void> putResult = cache.put("session123", session);
        
        if (putResult.isSuccess()) {
            System.out.println("Session cached in both L1 and L2");
        }
        
        // 获取会话数据（优先从L1获取，L1未命中时从L2获取并回写到L1）
        Try<Option<Session>> getResult = cache.get("session123", Session.class);
        if (getResult.isSuccess() && getResult.get().isDefined()) {
            Session cachedSession = getResult.get().get();
            System.out.println("Session found: " + cachedSession.getUserId());
        }
    }
    
    /**
     * 监控和链路追踪示例
     */
    public static void monitoringExample() {
        CacheOperations cache = Caches.local("config:{}")
            .withMetrics("app.config.cache")
            .withTrace("config-service")
            .onError(error -> System.err.println("Cache error: " + error.getMessage()));
        
        // 批量操作
        Map<String, Object> configs = new HashMap<>();
        configs.put("app.name", "MyApp");
        configs.put("app.version", "1.0.0");
        configs.put("app.debug", true);
        
        Try<Void> multiPutResult = cache.multiPut(configs, Duration.ofHours(24));
        if (multiPutResult.isSuccess()) {
            System.out.println("Configurations cached successfully");
        }
        
        // 批量获取
        Try<Map<String, String>> multiGetResult = cache.multiGet(
            Arrays.asList("app.name", "app.version"), String.class);
        
        if (multiGetResult.isSuccess()) {
            Map<String, String> results = multiGetResult.get();
            results.forEach((key, value) -> 
                System.out.println(key + " = " + value));
        }
        
        // 获取缓存统计
        Try<group.rxcloud.vrml.cache.api.CacheStats> statsResult = cache.getStats();
        if (statsResult.isSuccess()) {
            group.rxcloud.vrml.cache.api.CacheStats stats = statsResult.get();
            System.out.printf("Cache stats - Hit rate: %.2f%%, Size: %d%n", 
                stats.getHitRate() * 100, stats.getSize());
        }
    }
    
    /**
     * 自定义配置示例
     */
    public static void customConfigurationExample() {
        // Caffeine缓存配置
        CaffeineCacheConfiguration caffeineConfig = CaffeineCacheConfiguration.caffeineBuilder()
            .initialCapacity(100)
            .maxSize(5000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .recordStats(true)
            .build();
        
        CacheOperations caffeineCache = Caches.local("temp:{}", caffeineConfig);
        
        // 使用自定义配置的缓存
        Try<String> result = caffeineCache.getOrLoad("temp123", String.class, 
            () -> "temporary data", Duration.ofMinutes(5));
        
        if (result.isSuccess()) {
            System.out.println("Temporary data: " + result.get());
        }
    }
    
    // 辅助方法和类
    
    private static User createUser(String id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }
    
    private static Product loadProductFromDatabase(String productId) {
        // 模拟数据库查询
        if ("456".equals(productId)) {
            Product product = new Product();
            product.setId(productId);
            product.setName("Sample Product");
            product.setPrice(99.99);
            return product;
        }
        return null;
    }
    
    private static Session createSession(String sessionId, String userId) {
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setCreatedAt(System.currentTimeMillis());
        return session;
    }
    
    // 示例数据类
    
    public static class User {
        private String id;
        private String name;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    public static class Product {
        private String id;
        private String name;
        private double price;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
    
    public static class Session {
        private String sessionId;
        private String userId;
        private long createdAt;
        
        // Getters and setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Basic Cache Operations ===");
        basicCacheOperations();
        
        System.out.println("\n=== Cache with Protection ===");
        cacheWithProtection();
        
        System.out.println("\n=== Monitoring Example ===");
        monitoringExample();
        
        System.out.println("\n=== Custom Configuration ===");
        customConfigurationExample();
    }
}