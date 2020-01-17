package com.ten.func.vavr.test;

import org.apache.logging.log4j.message.FormattedMessage;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An <code>TestUnexpectedException</code> is thrown if the unit
 * test cache a unexpected result.
 *
 * @author shihaowang
 * @date 2020/1/8
 */
public class TestUnexpectedException extends RuntimeException {

    public TestUnexpectedException() {
        super();
    }

    public TestUnexpectedException(String message) {
        super(message);
    }

    public TestUnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestUnexpectedException(Throwable cause) {
        super(cause);
    }

    protected TestUnexpectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructor a {@link TestUnexpectedException} with the given message format, and arguments.
     *
     * @param format the format message
     * @param args   Arguments referenced by the format specifiers in the format string
     */
    public TestUnexpectedException(String format, Object... args) {
        super(new FormattedMessage(format, args).getFormattedMessage());
        if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
            this.initCause((Throwable) args[args.length - 1]);
        }
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return super.initCause(cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(stackTrace);
    }
}
