package group.rxcloud.vrml.core.api;

/**
 * VRML API标记接口
 * 所有VRML模块的API接口都应该继承此接口，用于统一标识和管理
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public interface VrmlApi {
    
    /**
     * 获取API名称
     * 
     * @return API名称
     */
    default String getApiName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * 获取API版本
     * 
     * @return API版本
     */
    default String getApiVersion() {
        return "1.2.0";
    }
}