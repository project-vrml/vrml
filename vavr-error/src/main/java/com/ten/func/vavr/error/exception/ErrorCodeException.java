package com.ten.func.vavr.error.exception;

import com.ten.func.vavr.error.code.ErrorCodeContext;

/**
 * The abstract implementation of exception.
 *
 * @apiNote All exception must have {@link ErrorCodeContext}
 */
public abstract class ErrorCodeException extends RuntimeException {

    private final ErrorCodeContext errorInfo;

    /**
     * Constructs a new exception with {@link ErrorCodeContext}
     *
     * @param errorInfo the error info.
     */
    public ErrorCodeException(ErrorCodeContext errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }

    /**
     * Constructs a new exception with {@link ErrorCodeContext} and {@link Throwable}.
     *
     * @param errorInfo the error info.
     * @param cause     the cause for this exception.
     */
    public ErrorCodeException(ErrorCodeContext errorInfo, Throwable cause) {
        super(errorInfo.getMessage(), cause);
        this.errorInfo = errorInfo;
    }

    /**
     * Returns the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return this.errorInfo.getCode();
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return this.errorInfo.getMessage();
    }
}

