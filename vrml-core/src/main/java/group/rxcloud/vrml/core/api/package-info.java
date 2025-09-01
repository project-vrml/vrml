/**
 * VRML核心API抽象包
 * 
 * <p>提供VRML框架的核心API抽象，包括：</p>
 * 
 * <ul>
 *   <li>{@link group.rxcloud.vrml.core.api.VrmlApi} - API标记接口</li>
 *   <li>{@link group.rxcloud.vrml.core.api.VrmlOperations} - 统一操作接口</li>
 *   <li>{@link group.rxcloud.vrml.core.api.VrmlConfiguration} - 统一配置接口</li>
 *   <li>{@link group.rxcloud.vrml.core.api.AbstractVrmlOperations} - 抽象基类</li>
 *   <li>{@link group.rxcloud.vrml.core.api.VrmlException} - 统一异常类</li>
 *   <li>{@link group.rxcloud.vrml.core.api.VrmlConfigurationManager} - 配置管理器</li>
 * </ul>
 * 
 * <p>所有VRML模块的API都应该继承这些基础接口，以确保API的一致性和可集成性。</p>
 * 
 * @author VRML Team
 * @since 1.2.0
 */
package group.rxcloud.vrml.core.api;