# VRML Cache Module

VRML缓存抽象层模块，提供统一的缓存操作API，支持多种缓存实现和高级功能。

## 特性

- **统一API**: 提供一致的缓存操作接口，支持不同的缓存实现
- **多级缓存**: 支持L1(本地)和L2(远程)缓存的协调
- **防护机制**: 内置缓存穿透、击穿、雪崩防护
- **监控集成**: 自动收集性能指标和健康状态
- **SPI扩展**: 支持通过SPI机制扩展新的缓存适配器
- **函数式编程**: 基于Vavr的Try和Option类型，优雅处理异常

## 支持的缓存类型

- **Redis**: 基于Spring Data Redis的分布式缓存
- **Caffeine**: 高性能本地缓存
- **Multilevel**: 多级缓存策略，结合本地和远程缓存

## 快速开始

### 基本用法

```java
// Redis缓存
CacheOperations redisCache = Caches.redis("user:{}");
Try<Void> putResult = redisCache.put("123", user, Duration.ofHours(1));
Try<Option<User>> getResult = redisCache.get("123", User.class);

// 本地缓存
CacheOperations localCache = Caches.local("config:{}");
Try<String> value = localCache.getOrLoad("app.name", String.class, 
    () -> configService.getAppName());

// 多级缓存
CacheOperations multilevelCache = Caches.multilevel("product:{}");
Try<Product> product = multilevelCache.getOrLoad("123", Product.class, 
    () -> productService.findById(123), Duration.ofMinutes(30));
```

### 监控和链路追踪

```java
CacheOperations cache = Caches.redis("user:{}")
    .withMetrics("user.cache")
    .withTrace("user-service")
    .onError(error -> log.error("Cache error", error));

Try<Option<User>> result = cache.get("123", User.class);
```

### 自定义配置

```java
RedisCacheConfiguration config = RedisCacheConfiguration.builder()
    .host("localhost")
    .port(6379)
    .database(0)
    .defaultTtl(Duration.ofHours(2))
    .maxSize(10000)
    .avalancheProtectionEnabled(true)
    .hotKeyProtectionEnabled(true)
    .build();

CacheOperations cache = Caches.redis("user:{}", config);
```

## 防护机制

### 缓存穿透防护

使用布隆过滤器防止查询不存在的数据：

```java
DefaultCacheConfiguration config = new DefaultCacheConfiguration();
config.setBloomFilterEnabled(true);

CacheOperations cache = Caches.redis("user:{}", config);
```

### 缓存击穿防护

热点key并发访问保护：

```java
DefaultCacheConfiguration config = new DefaultCacheConfiguration();
config.setHotKeyProtectionEnabled(true);

CacheOperations cache = Caches.redis("user:{}", config);
```

### 缓存雪崩防护

TTL随机化避免同时过期：

```java
DefaultCacheConfiguration config = new DefaultCacheConfiguration();
config.setAvalancheProtectionEnabled(true);
config.setRandomTtlRange(Duration.ofMinutes(5));

CacheOperations cache = Caches.redis("user:{}", config);
```

## 多级缓存

配置L1(本地)和L2(远程)缓存：

```java
MultilevelCacheConfiguration config = MultilevelCacheConfiguration.builder()
    .l1CacheType("caffeine")
    .l2CacheType("redis")
    .l1MaxSize(1000)
    .l2MaxSize(10000)
    .l1DefaultTtl(Duration.ofMinutes(10))
    .l2DefaultTtl(Duration.ofHours(1))
    .writeStrategy(MultilevelCacheConfiguration.WriteStrategy.WRITE_THROUGH)
    .l1WriteBackEnabled(true)
    .build();

CacheOperations cache = Caches.multilevel("product:{}", config);
```

## 监控

### 性能指标

缓存模块自动收集以下指标：

- 命中率 (hit_rate)
- 未命中率 (miss_rate)
- 错误率 (error_rate)
- 平均响应时间 (avg_response_time)
- 最大响应时间 (max_response_time)
- 缓存大小 (size)
- 驱逐次数 (eviction_count)

### 健康检查

```java
Try<Boolean> healthy = cache.healthCheck();
if (healthy.isSuccess() && healthy.get()) {
    log.info("Cache is healthy");
} else {
    log.warn("Cache is unhealthy");
}
```

### 统计信息

```java
Try<CacheStats> stats = cache.getStats();
if (stats.isSuccess()) {
    CacheStats cacheStats = stats.get();
    log.info("Hit rate: {:.2f}%", cacheStats.getHitRate() * 100);
    log.info("Cache size: {}", cacheStats.getSize());
}
```

## 扩展

### 自定义缓存提供者

实现`CacheProvider`接口：

```java
public class CustomCacheProvider implements CacheProvider {
    
    @Override
    public String getName() {
        return "Custom Cache Provider";
    }
    
    @Override
    public String getType() {
        return "custom";
    }
    
    @Override
    public CacheOperations createOperations(String pattern, CacheConfiguration config) {
        return new CustomCacheOperations(pattern, config);
    }
    
    @Override
    public boolean supports(String type) {
        return "custom".equals(type);
    }
}
```

在`META-INF/services/group.rxcloud.vrml.cache.spi.CacheProvider`文件中注册：

```
com.example.CustomCacheProvider
```

## 配置参考

### 通用配置

| 属性 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| `cacheType` | String | "caffeine" | 缓存类型 |
| `defaultTtl` | Duration | 1小时 | 默认过期时间 |
| `maxSize` | long | 10000 | 最大缓存大小 |
| `enabled` | boolean | true | 是否启用 |
| `metricsEnabled` | boolean | true | 是否启用监控 |
| `traceEnabled` | boolean | true | 是否启用链路追踪 |

### Redis配置

| 属性 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| `host` | String | "localhost" | Redis主机 |
| `port` | int | 6379 | Redis端口 |
| `database` | int | 0 | 数据库索引 |
| `password` | String | null | 密码 |
| `connectionTimeout` | Duration | 5秒 | 连接超时 |
| `readTimeout` | Duration | 3秒 | 读取超时 |

### Caffeine配置

| 属性 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| `initialCapacity` | int | 100 | 初始容量 |
| `concurrencyLevel` | int | 4 | 并发级别 |
| `expireAfterWrite` | Duration | null | 写入后过期 |
| `expireAfterAccess` | Duration | null | 访问后过期 |
| `weakKeys` | boolean | false | 弱键引用 |
| `weakValues` | boolean | false | 弱值引用 |

## 最佳实践

1. **选择合适的缓存类型**：
   - 本地缓存适用于读多写少的配置数据
   - Redis适用于分布式环境下的共享数据
   - 多级缓存适用于热点数据的高性能访问

2. **合理设置TTL**：
   - 根据数据更新频率设置合适的过期时间
   - 启用雪崩防护避免大量缓存同时过期

3. **监控缓存性能**：
   - 关注命中率，低命中率可能需要调整缓存策略
   - 监控响应时间，及时发现性能问题

4. **异常处理**：
   - 使用Try类型优雅处理缓存异常
   - 设置合适的错误处理回调

5. **资源管理**：
   - 及时清理不再使用的缓存实例
   - 合理设置缓存大小避免内存溢出

## 依赖

```xml
<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-cache</artifactId>
    <version>1.2.0</version>
</dependency>

<!-- 可选依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```