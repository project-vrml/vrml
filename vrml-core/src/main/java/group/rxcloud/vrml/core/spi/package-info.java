/**
 * VRML SPI服务提供者机制包
 * 
 * <p>提供可插拔的服务提供者接口，支持：</p>
 * 
 * <ul>
 *   <li>{@link group.rxcloud.vrml.core.spi.VrmlProvider} - 服务提供者接口</li>
 *   <li>{@link group.rxcloud.vrml.core.spi.VrmlProviderRegistry} - 提供者注册表</li>
 * </ul>
 * 
 * <p>通过SPI机制，各个模块可以提供可插拔的适配器实现，支持：</p>
 * <ul>
 *   <li>自动服务发现</li>
 *   <li>手动服务注册</li>
 *   <li>优先级排序</li>
 *   <li>健康检查</li>
 *   <li>默认提供者设置</li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 实现服务提供者
 * public class MyProvider implements VrmlProvider<MyOperations, MyConfiguration> {
 *     
 *     @Override
 *     public String getName() {
 *         return "my-provider";
 *     }
 *     
 *     @Override
 *     public String getType() {
 *         return "my-type";
 *     }
 *     
 *     @Override
 *     public MyOperations createOperations(String pattern, MyConfiguration config) {
 *         return new MyOperations();
 *     }
 *     
 *     @Override
 *     public boolean supports(String type) {
 *         return "my-type".equals(type);
 *     }
 * }
 * 
 * // 注册提供者
 * VrmlProviderRegistry.registerProvider(MyProvider.class, new MyProvider());
 * 
 * // 获取提供者
 * MyProvider provider = VrmlProviderRegistry.getProvider(MyProvider.class, "my-type");
 * }</pre>
 * 
 * @author VRML Team
 * @since 1.2.0
 */
package group.rxcloud.vrml.core.spi;