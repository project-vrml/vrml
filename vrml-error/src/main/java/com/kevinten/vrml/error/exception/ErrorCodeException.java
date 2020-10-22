package com.kevinten.vrml.error.exception;


import com.kevinten.vrml.error.code.ErrorCodeContext;

/**
 * The abstract implementation of error code exception.
 *
 * @apiNote All exception must have {@link ErrorCodeContext}
 */
public abstract class ErrorCodeException extends RuntimeException {

    private final ErrorCodeContext errorCodeContext;
    private final String errorMessage;

    /**
     * Constructs a new exception with {@link ErrorCodeContext}
     *
     * @param errorCodeContext the error info.
     */
    public ErrorCodeException(ErrorCodeContext errorCodeContext) {
        super(errorCodeContext.getMessage());
        this.errorCodeContext = errorCodeContext;
        this.errorMessage = errorCodeContext.getMessage();
    }

    /**
     * Constructs a new exception with {@link ErrorCodeContext} and {@link Throwable}.
     *
     * @param errorCodeContext the error info.
     * @param cause            the cause for this exception.
     */
    public ErrorCodeException(ErrorCodeContext errorCodeContext, Throwable cause) {
        super(errorCodeContext.getMessage(), cause);
        this.errorCodeContext = errorCodeContext;
        this.errorMessage = errorCodeContext.getMessage();
    }

    /**
     * Constructs a new exception with {@link ErrorCodeContext} and {@code errorMessage}
     *
     * @param errorCodeContext the error info.
     * @param errorMessage     the error message
     */
    public ErrorCodeException(ErrorCodeContext errorCodeContext, String errorMessage) {
        super(errorMessage);
        this.errorCodeContext = errorCodeContext;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new exception with {@link ErrorCodeContext} and {@code errorMessage} and {@link Throwable}.
     *
     * @param errorCodeContext the error info.
     * @param errorMessage     the error message
     * @param cause            the cause for this exception.
     */
    public ErrorCodeException(ErrorCodeContext errorCodeContext, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCodeContext = errorCodeContext;
        this.errorMessage = errorMessage;
    }

    /**
     * Gets error code context.
     *
     * @return the error code context
     */
    public ErrorCodeContext getErrorCodeContext() {
        return this.errorCodeContext;
    }

    /**
     * Returns the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return this.errorCodeContext.getCode();
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    // -- Experimental characteristics : Performance improvement

    /**
     * Using {@code synchronized} to lock exception object
     *
     * @see Throwable#fillInStackTrace()
     */
    private static boolean synchronizedFill = true;
    /**
     * Using native {@code fillInStackTrace} to get exception stack
     *
     * @see Throwable#fillInStackTrace()
     */
    private static boolean nativeStackFill = true;

    public static void openSynchronizedFill() {
        synchronizedFill = true;
    }

    public static void openNativeStackFill() {
        nativeStackFill = true;
    }

    public static void closeSynchronizedFill() {
        synchronizedFill = false;
    }

    public static void closeNativeStackFill() {
        nativeStackFill = false;
    }

    @Override
    public Throwable fillInStackTrace() {
        if (synchronizedFill) {
            synchronized (this) {
                if (nativeStackFill) {
                    return super.fillInStackTrace();
                } else {
                    return this;
                }
            }
        } else {
            if (nativeStackFill) {
                return super.fillInStackTrace();
            } else {
                return this;
            }
        }
    }
}

