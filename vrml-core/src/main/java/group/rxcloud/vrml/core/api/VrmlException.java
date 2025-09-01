package group.rxcloud.vrml.core.api;

/**
 * VRML统一异常类
 * 所有VRML模块的异常都应该继承此类
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class VrmlException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String module;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public VrmlException(String message) {
        super(message);
        this.errorCode = "VRML_ERROR";
        this.module = "unknown";
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public VrmlException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VRML_ERROR";
        this.module = "unknown";
    }
    
    /**
     * 构造函数
     * 
     * @param module 模块名称
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public VrmlException(String module, String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.module = module;
    }
    
    /**
     * 构造函数
     * 
     * @param module 模块名称
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因
     */
    public VrmlException(String module, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.module = module;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取模块名称
     * 
     * @return 模块名称
     */
    public String getModule() {
        return module;
    }
    
    @Override
    public String toString() {
        return String.format("[%s][%s] %s", module, errorCode, getMessage());
    }
}