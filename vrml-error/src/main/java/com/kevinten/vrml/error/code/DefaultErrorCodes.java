package com.kevinten.vrml.error.code;

/**
 * Application error info context:
 * [SEC-200000] SUCCESS
 * [SEC-10Xxxx] {@code parameter} error. Define your parameter check exception
 * [SEC-20Xxxx] {@code business} error. Define your business logic exception
 * [SEC-30Xxxx] {@code repository service}. Define repository operation exception
 * [SEC-40Xxxx] {@code dependent service}. Define dependency service exception
 * [SEC-50Xxxx] {@code system} error. Define application system exception
 */
public enum DefaultErrorCodes implements DefaultErrorCodeContext {

    /**
     * The successful error code.
     */
    SUCCESS("200000", "Success"),

    /*-------------------------------------------Parameter error as below---------------------------------------**/
    /**
     * Invalid basic parameter error, the code starts with 0.
     */
    PARAMETER_ERROR(
            GENERATOR.createParameterErrorCode("0000"), "Invalid parameter error!"),

    /*-------------------------------------------Business error as below---------------------------------------**/
    /**
     * Basic business error, the code starts with 0.
     */
    BUSINESS_ERROR(
            GENERATOR.createBusinessProcessErrorCode("0001"), "Business error!"),

    /*-------------------------------------------Repository error as below---------------------------------------**/
    /**
     * Basic repository error, the code starts with 0.
     */
    REPOSITORY_ERROR(
            GENERATOR.createRepositoryErrorCode("0000"), "Repository error!"),

    /*-------------------------------------------Dependent service error as below---------------------------------------**/
    /**
     * Basic dependent service error, the code starts with 0.
     */
    DEPENDENT_SERVICE_ERROR(
            GENERATOR.createDependentServiceErrorCode("0000"), "Failed to call the dependent service!"),

    /*-------------------------------------------System error as below---------------------------------------**/
    /**
     * Basic system error, the code starts with 0.
     */
    SYSTEM_ERROR(
            GENERATOR.createSystemErrorCode("0000"), "System error!"),
    ;

    // -- Encapsulation

    private final String code;
    private final String message;

    DefaultErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    static {
        ErrorCodeManager.assertErrorCodesNoDuplicate(DefaultErrorCodes.values());
    }

    /**
     * Show error codes.
     */
    public static void showErrorCodes() {
        MANAGER.showErrorCodes(DefaultErrorCodes.values());
    }
}
