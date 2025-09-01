/**
 * VRML集成管理包
 * 
 * <p>提供统一的集成管理功能，包括：</p>
 * 
 * <ul>
 *   <li>{@link group.rxcloud.vrml.core.integration.VrmlIntegrationManager} - 集成管理器</li>
 *   <li>{@link group.rxcloud.vrml.core.integration.VrmlMetricIntegration} - 监控指标集成</li>
 *   <li>{@link group.rxcloud.vrml.core.integration.VrmlLogIntegration} - 日志集成</li>
 *   <li>{@link group.rxcloud.vrml.core.integration.VrmlAlertIntegration} - 告警集成</li>
 *   <li>{@link group.rxcloud.vrml.core.integration.VrmlTraceIntegration} - 链路追踪集成</li>
 * </ul>
 * 
 * <p>这些集成类提供了与VRML其他模块的统一集成接口，支持：</p>
 * <ul>
 *   <li>模块可用性检测</li>
 *   <li>自动降级处理</li>
 *   <li>统一的集成配置</li>
 *   <li>错误处理和日志记录</li>
 * </ul>
 * 
 * <p>所有集成都是可选的，如果相应的模块不存在，集成功能会自动降级。</p>
 * 
 * @author VRML Team
 * @since 1.2.0
 */
package group.rxcloud.vrml.core.integration;