package com.ten.func.vavr.error.code;


import com.ten.func.vavr.core.tags.Unused;
import com.ten.func.vavr.error.scheme.ErrorSchemeContext;

import static com.ten.func.vavr.error.code.ErrorCodeContext.ErrorCodeGenerator.*;

/**
 * Application error info context:
 * <p>
 * [20000] SUCCESS
 * <p>
 * [1Xxxx] {@code parameter} error. Define your parameter check exception
 * [2Xxxx] {@code business} error. Define your business logic exception
 * [3Xxxx] {@code repository service}. Define repository operation exception
 * [4Xxxx] {@code dependent service}. Define dependency service exception
 * [5Xxxx] {@code system} error. Define application system exception
 */
public enum ErrorCodes implements ErrorCodeContext {

    /**
     * The successful error code.
     */
    SUCCESS("20000", "Success"),

    /*-------------------------------------------Parameter error as below---------------------------------------**/
    /**
     * Invalid basic parameter error, the code starts with 0.
     */
    PARAMETER_ERROR(
            createParameterErrorCode("000"), "Invalid parameter error!"),

    /*-------------------------------------------Business error as below---------------------------------------**/
    /**
     * Basic business error, the code starts with 0.
     */
    BUSINESS_ERROR(
            createBusinessProcessErrorCode("001"), "Business error!"),

    /*-------------------------------------------Repository error as below---------------------------------------**/
    /**
     * Basic repository error, the code starts with 0.
     */
    REPOSITORY_ERROR(
            createRepositoryErrorCode("000"), "Repository error!"),

    /*-------------------------------------------Dependent service error as below---------------------------------------**/
    /**
     * Basic dependent service error, the code starts with 0.
     */
    DEPENDENT_SERVICE_ERROR(
            createDependentServiceErrorCode("000"), "Failed to call the dependent service!"),

    /*-------------------------------------------System error as below---------------------------------------**/
    /**
     * Basic system error, the code starts with 0.
     */
    SYSTEM_ERROR(
            createSystemErrorCode("000"), "System error!"),
    ;

    // -- Encapsulation

    private String code;
    private String message;

    ErrorCodes(String code, String message) {
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

    @Unused
    @Override
    public ErrorSchemeContext getScheme() {
        return null;
    }

    @Override
    public String toString() {
        return this.code + " : " + this.message;
    }

    static {
        ErrorCodeManager.assertErrorCodesNoDuplicate(ErrorCodes.values());
    }

    /**
     * Show error codes.
     */
    public static void showErrorCodes() {
        ErrorCodeManager.showErrorCodes(ErrorCodes.values());
    }
}
