/**
 * VRML核心模块
 * 
 * <p>提供VRML框架的核心抽象和基础设施，包括：</p>
 * 
 * <ul>
 *   <li><strong>API抽象</strong>: 统一的API接口规范和基础类</li>
 *   <li><strong>SPI机制</strong>: 可插拔的服务提供者接口</li>
 *   <li><strong>集成管理</strong>: 监控、日志、告警、链路追踪的统一集成</li>
 *   <li><strong>配置管理</strong>: 多源配置管理和类型转换</li>
 *   <li><strong>Spring集成</strong>: Spring框架的集成支持</li>
 * </ul>
 * 
 * <h2>主要包结构</h2>
 * <ul>
 *   <li>{@link group.rxcloud.vrml.core.api} - 核心API抽象</li>
 *   <li>{@link group.rxcloud.vrml.core.spi} - SPI服务提供者机制</li>
 *   <li>{@link group.rxcloud.vrml.core.integration} - 集成管理</li>
 *   <li>{@link group.rxcloud.vrml.core.beans} - Spring Bean管理</li>
 *   <li>{@link group.rxcloud.vrml.core.serialization} - 序列化工具</li>
 *   <li>{@link group.rxcloud.vrml.core.tags} - 代码标记注解</li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 实现自定义操作接口
 * public class MyOperations extends AbstractVrmlOperations {
 *     
 *     @Override
 *     protected AbstractVrmlOperations createCopy() {
 *         return new MyOperations(this);
 *     }
 *     
 *     public Try<String> doSomething(String input) {
 *         return executeWithIntegration("doSomething", () -> {
 *             // 业务逻辑
 *             return Try.success("result: " + input);
 *         });
 *     }
 * }
 * 
 * // 使用监控和链路追踪
 * MyOperations ops = new MyOperations()
 *     .withMetrics("my.service")
 *     .withTrace("my.operation")
 *     .onError(error -> log.error("Operation failed", error));
 * 
 * Try<String> result = ops.doSomething("test");
 * }</pre>
 * 
 * @author VRML Team
 * @since 1.2.0
 * @version 1.2.0
 */
package group.rxcloud.vrml.core;