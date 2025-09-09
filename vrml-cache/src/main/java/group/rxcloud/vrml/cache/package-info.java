/**
 * VRML缓存抽象层模块
 * 
 * <p>提供统一的缓存操作API，支持多种缓存实现和高级功能：</p>
 * 
 * <ul>
 *   <li><strong>统一API</strong> - 一致的缓存操作接口，支持不同的缓存实现</li>
 *   <li><strong>多级缓存</strong> - L1(本地)和L2(远程)缓存的智能协调</li>
 *   <li><strong>防护机制</strong> - 内置缓存穿透、击穿、雪崩防护</li>
 *   <li><strong>监控集成</strong> - 自动收集性能指标和健康状态</li>
 *   <li><strong>SPI扩展</strong> - 支持通过SPI机制扩展新的缓存适配器</li>
 *   <li><strong>函数式编程</strong> - 基于Vavr的Try和Option类型，优雅处理异常</li>
 * </ul>
 * 
 * <h2>快速开始</h2>
 * 
 * <pre>{@code
 * // Redis缓存
 * CacheOperations redisCache = Caches.redis("user:{}");
 * Try<Void> putResult = redisCache.put("123", user, Duration.ofHours(1));
 * Try<Option<User>> getResult = redisCache.get("123", User.class);
 * 
 * // 本地缓存
 * CacheOperations localCache = Caches.local("config:{}");
 * Try<String> value = localCache.getOrLoad("app.name", String.class, 
 *     () -> configService.getAppName());
 * 
 * // 多级缓存
 * CacheOperations multilevelCache = Caches.multilevel("product:{}");
 * Try<Product> product = multilevelCache.getOrLoad("123", Product.class, 
 *     () -> productService.findById(123), Duration.ofMinutes(30));
 * }</pre>
 * 
 * <h2>支持的缓存类型</h2>
 * 
 * <ul>
 *   <li><strong>Redis</strong> - 基于Spring Data Redis的分布式缓存</li>
 *   <li><strong>Caffeine</strong> - 高性能本地缓存</li>
 *   <li><strong>Multilevel</strong> - 多级缓存策略，结合本地和远程缓存</li>
 * </ul>
 * 
 * <h2>防护机制</h2>
 * 
 * <ul>
 *   <li><strong>缓存穿透防护</strong> - 使用布隆过滤器防止查询不存在的数据</li>
 *   <li><strong>缓存击穿防护</strong> - 热点key并发访问保护</li>
 *   <li><strong>缓存雪崩防护</strong> - TTL随机化避免同时过期</li>
 * </ul>
 * 
 * @author VRML Team
 * @since 1.2.0
 * @version 1.2.0
 */
package group.rxcloud.vrml.cache;