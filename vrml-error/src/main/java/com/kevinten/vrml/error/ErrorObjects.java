package com.kevinten.vrml.error;


import com.kevinten.vrml.error.code.ErrorCodeContext;
import com.kevinten.vrml.error.exception.ErrorCodeException;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class consists of {@code static} utility methods for operating
 * on objects.  These utilities include {@code null}-safe methods with
 * {@code error code} for checking objects.
 *
 * @since 1.0.0
 */
public final class ErrorObjects {

    private ErrorObjects() {
        throw new AssertionError("No $ErrorObjects instances for you!");
    }

    private static Function<ErrorCodeContext, ErrorCodeException> globalExceptionSupply;

    /**
     * Configures the {@link ErrorObjects} so {@link #requireNonNull(Object, ErrorCodeContext)} will
     * accept a {@link ErrorCodeContext} and apply to the {@link #globalExceptionSupply} to supply a
     * {@link ErrorCodeException}
     *
     * @param globalExceptionSupply the global exception supply
     */
    public static void setGlobalExceptionSupply(Function<ErrorCodeContext, ErrorCodeException> globalExceptionSupply) {
        ErrorObjects.globalExceptionSupply = globalExceptionSupply;
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link ErrorCodeException} if it is.
     *
     * @param <T>     the type of the reference
     * @param <Excep> the specific instance type extends {@code ErrorCodeException}
     * @param obj     an object
     * @param t       supplier of the detail exception instance to be
     *                used in the event that a {@code ErrorCodeException} is thrown
     * @return {@code obj} if not {@code null}
     * @throws ErrorCodeException   if {@code obj} is {@code null}
     * @throws NullPointerException if {@code t} is {@code null}
     */
    public static <T, Excep extends ErrorCodeException> T requireNonNull(T obj, Supplier<Excep> t) {
        if (obj == null) {
            Objects.requireNonNull(t);
            throw t.get();
        }
        return obj;
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link ErrorCodeException} with {@link ErrorCodeContext} if it is.
     *
     * @param <T>    the type of the reference
     * @param <Code> the specific enum instance extends {@code ErrorCodeContext}
     * @param obj    an object
     * @param code   supplier of the detail exception instance to be
     *               used in the event that a {@code ErrorCodeException} with
     *               {@code ErrorCodeContext} is thrown
     * @return {@code obj} if not {@code null}
     * @throws ErrorCodeException   if {@code obj} is {@code null}
     * @throws NullPointerException if {@link #globalExceptionSupply} is {@code null}
     */
    public <T, Code extends ErrorCodeContext> T requireNonNull(T obj, Code code) {
        if (obj == null) {
            Objects.requireNonNull(globalExceptionSupply);
            throw globalExceptionSupply.apply(code);
        }
        return obj;
    }

    /**
     * Creates an instance of {@link RequireHelper} .
     *
     * @param exceptionSupply the exception supply
     * @return the require helper
     */
    public static RequireHelper requireHelper(Function<ErrorCodeContext, ErrorCodeException> exceptionSupply) {
        return new RequireHelper(exceptionSupply);
    }

    /**
     * Creates an instance of {@link RequireHelper}.
     *
     * @param exceptionSupply the exception supply
     * @param defaultCode     the default error code
     * @return the require helper
     */
    public static RequireHelper requireHelper(Function<ErrorCodeContext, ErrorCodeException> exceptionSupply, ErrorCodeContext defaultCode) {
        return new RequireHelper(exceptionSupply, defaultCode);
    }

    /**
     * Support class for {@link ErrorObjects#requireHelper}.
     */
    public static final class RequireHelper {

        private final Function<ErrorCodeContext, ErrorCodeException> exceptionSupply;
        private final ErrorCodeContext nullDefaultCode;

        /**
         * Use {@link ErrorObjects#requireHelper(Function)} to create an instance.
         */
        private RequireHelper(Function<ErrorCodeContext, ErrorCodeException> exceptionSupply) {
            this.exceptionSupply = Objects.requireNonNull(exceptionSupply);
            this.nullDefaultCode = null;
        }

        /**
         * Use {@link ErrorObjects#requireHelper(Function, ErrorCodeContext)} to create an instance with {@link #nullDefaultCode}.
         */
        private RequireHelper(Function<ErrorCodeContext, ErrorCodeException> exceptionSupply, ErrorCodeContext nullDefaultCode) {
            this.exceptionSupply = Objects.requireNonNull(exceptionSupply);
            this.nullDefaultCode = Objects.requireNonNull(nullDefaultCode);
        }

        /**
         * Checks that the specified object reference is not {@code null} and
         * throws a customized {@link ErrorCodeException} with {@link #nullDefaultCode} if it is.
         *
         * @param <T> the type of the reference
         * @param obj an object
         * @return {@code obj} if not {@code null}
         * @throws ErrorCodeException if {@code obj} is {@code null}
         */
        public <T> T requireNonNull(T obj) {
            if (obj == null) {
                throw exceptionSupply.apply(this.nullDefaultCode);
            }
            return obj;
        }

        /**
         * Checks that the specified object reference is not {@code null} and
         * throws a customized {@link ErrorCodeException} with {@link ErrorCodeContext} if it is.
         *
         * @param <T>    the type of the reference
         * @param <Code> the specific enum instance extends {@code ErrorCodeContext}
         * @param obj    an object
         * @param code   supplier of the detail exception instance to be
         *               used in the event that a {@code ErrorCodeException} with
         * @return {@code obj} if not {@code null}
         * @throws ErrorCodeException if {@code obj} is {@code null}
         */
        public <T, Code extends ErrorCodeContext> T requireNonNull(T obj, Code code) {
            if (obj == null) {
                throw exceptionSupply.apply(code);
            }
            return obj;
        }

        /**
         * Asserts that a condition is true. If it isn't it throws an
         * {@link ErrorCodeException} with the given {@link #nullDefaultCode}.
         *
         * @param condition the condition to be checked
         */
        public void assertTrue(boolean condition) {
            if (!condition) {
                throw exceptionSupply.apply(this.nullDefaultCode);
            }
        }

        /**
         * Asserts that a condition is true. If it isn't it throws an
         * {@link ErrorCodeException} with the given {@link ErrorCodeContext}.
         *
         * @param condition the condition to be checked
         * @param <Code>    the specific enum instance extends {@code ErrorCodeContext}
         * @param code      supplier of the detail exception instance to be
         *                  used in the event that a {@code ErrorCodeException} with
         */
        public <Code extends ErrorCodeContext> void assertTrue(boolean condition, Code code) {
            if (!condition) {
                throw exceptionSupply.apply(code);
            }
        }

        /**
         * Asserts that a condition is true. If it isn't it throws an
         * {@link ErrorCodeException} with the given {@link #nullDefaultCode}.
         *
         * @param condition the condition to be checked
         */
        public void assertTrue(BooleanSupplier condition) {
            if (!condition.getAsBoolean()) {
                throw exceptionSupply.apply(this.nullDefaultCode);
            }
        }

        /**
         * Asserts that a condition is true. If it isn't it throws an
         * {@link ErrorCodeException} with the given {@link ErrorCodeContext}.
         *
         * @param condition the condition to be checked
         * @param <Code>    the specific enum instance extends {@code ErrorCodeContext}
         * @param code      supplier of the detail exception instance to be
         *                  used in the event that a {@code ErrorCodeException} with
         */
        public <Code extends ErrorCodeContext> void assertTrue(BooleanSupplier condition, Code code) {
            if (!condition.getAsBoolean()) {
                throw exceptionSupply.apply(code);
            }
        }
    }
}
