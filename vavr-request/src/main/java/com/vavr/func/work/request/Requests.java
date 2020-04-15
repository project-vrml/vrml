package com.vavr.func.work.request;

import com.vavr.func.work.request.config.RequestConfiguration;
import com.vavr.func.work.request.report.RequestReport;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.vavr.func.work.request.RequestModule.sneakyThrow;


/**
 * <pre>
 * Why do I provide this API?
 *
 * Because there are a lot of problems in the {@code soa} request.
 *
 * Please ask yourself:
 *
 * 1. 调用接口后，未抛出异常就视作成功吗？
 * 2. 即使未抛出异常，考虑过返回状态码为非成功码的情况吗？
 * 2. 考虑过统计非成功码的出现原因和出现频率吗？
 *
 * If you say "no!". I think you need this API to regulate your behavior.
 *
 * This will force you to think above all and provide support.
 * </pre>
 *
 * @param <T> the type parameter
 */
public interface Requests<T> {

    /**
     * This is a API demo.
     *
     * @param args the args
     */
    static void main(String[] args) {
        Object thisIsaResponse = new Object();
        // response
        Requests.of(() -> thisIsaResponse)
                .check(o -> {
                    // check response code and others
                    if (Math.random() > 0.5) {
                        // you can throws exception when check illegal
                        throw new RuntimeException();
                    }
                })
                .record(o -> {
                    // record response
                    return new RequestConfiguration.RequestReportValue.ReportBuilder("default")
                            // request value (you can supply response code)
                            .recordValue(o.getClass().getSimpleName())
                            // use remote config
                            .useConfig()
                            .build();
                })
                // throws when failure
                .get();
    }

    /**
     * Creates a Request of a value.
     *
     * @param <T>   component type
     * @param value A value
     * @return {@code Success(value)}
     */
    static <T> Requests<T> of(T value) {
        Objects.requireNonNull(value, "value is null");
        return new Success<>(value);
    }

    /**
     * Creates a Request of a Checked Supplier.
     *
     * @param <T>      component type
     * @param supplier A Checked Supplier
     * @return {@code Success(supplier.get())} if no exception occurs, otherwise {@code Failure(throwable)} if an exception occurs calling {@code supplier.get()}.
     */
    static <T> Requests<T> of(CheckedFunction0<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        try {
            return new Success<>(supplier.apply());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Creates a Request of a CheckedRunnable.
     *
     * @param runnable A checked runnable
     * @return {@code Success(null)} if no exception occurs, otherwise {@code Failure(throwable)} if an exception occurs calling {@code runnable.run()}.
     */
    static Requests<Void> of(CheckedRunnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        try {
            runnable.run();
            return new Success<>(null);
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Gets the result of this Request if this is a {@code Success} or throws if this is a {@code Failure}.
     * <strong>IMPORTANT! If this is a {@link Failure}, the underlying {@code cause} of type {@link Throwable} is thrown.</strong>
     * The thrown exception is exactly the same as the result of {@link #getCause()}.
     *
     * @return The result of this {@code Request}.
     */
    T get();

    /**
     * Gets the cause if this is a Failure or throws if this is a Success.
     *
     * @return The cause if this is a Failure
     * @throws UnsupportedOperationException if this is a Success
     */
    Throwable getCause();

    /**
     * Checks if this is a Failure.
     *
     * @return true, if this is a Failure, otherwise false, if this is a Success
     */
    boolean isFailure();

    /**
     * Checks if this is a Success.
     *
     * @return true, if this is a Success, otherwise false, if this is a Failure
     */
    boolean isSuccess();

    /**
     * Consumes the cause if this is a {@link Failure}.
     *
     * <pre>{@code
     * // (does not print anything)
     * Request.success(1).onFailure(System.out::println);
     *
     * // prints "java.lang.Error"
     * Request.failure(new Error()).onFailure(System.out::println);
     * }*</pre>
     *
     * @param action An exception consumer
     * @return this requests
     * @throws NullPointerException if {@code action} is null
     */
    default Requests<T> onFailure(Consumer<? super Throwable> action) {
        Objects.requireNonNull(action, "action is null");
        if (isFailure()) {
            action.accept(getCause());
        }
        return this;
    }

    /**
     * Consumes the value if this is a {@link Success}.
     *
     * <pre>{@code
     * // prints "1"
     * Request.success(1).onSuccess(System.out::println);
     *
     * // (does not print anything)
     * Request.failure(new Error()).onSuccess(System.out::println);
     * }*</pre>
     *
     * @param action A value consumer
     * @return this requests
     * @throws NullPointerException if {@code action} is null
     */
    default Requests<T> onSuccess(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess()) {
            action.accept(get());
        }
        return this;
    }

    /**
     * Applies the action to the value of a Success or does nothing in the case of a Failure.
     *
     * @param record A Consumer
     * @return this {@code Request}
     * @throws NullPointerException if {@code record} is null
     */
    default Requests<T> record(Function<? super T, RequestConfiguration.RequestReportValue> record) {
        Objects.requireNonNull(record, "record is null");
        if (isSuccess()) {
            RequestConfiguration.RequestReportValue result = record.apply(get());
            RequestReport.registerRequest(result);
        }
        return this;
    }

    /**
     * Returns {@code this}, if this is a {@code Success}, otherwise tries to recover the exception of the failure with {@code f},
     * i.e. calling {@code Request.of(() -> f.apply(throwable))}.
     *
     * <pre>{@code
     * // = Success(13)
     * Request.of(() -> 27/2).recover(x -> Integer.MAX_VALUE);
     *
     * // = Success(2147483647)
     * Request.of(() -> 1/0).recover(x -> Integer.MAX_VALUE);
     * }*</pre>
     *
     * @param recover A recovery function taking a Throwable
     * @return a {@code Request}
     * @throws NullPointerException if {@code recover} is null
     */
    default Requests<T> recover(Function<? super Throwable, ? extends T> recover) {
        Objects.requireNonNull(recover, "recover is null");
        if (isFailure()) {
            return Requests.of(() -> recover.apply(getCause()));
        } else {
            return this;
        }
    }

    /**
     * Applies the action to the value of a Success or does nothing in the case of a Failure.
     *
     * @param check A Consumer check
     * @return this {@code Request}
     * @throws NullPointerException if {@code check} is null
     */
    default Requests<T> check(Consumer<? super T> check) {
        Objects.requireNonNull(check, "check is null");
        if (isSuccess()) {
            return of(() -> {
                check.accept(get());
                return get();
            });
        }
        return this;
    }

    // -- Get Value

    /**
     * Or else requests.
     *
     * @param other the other
     * @return the requests
     */
    @SuppressWarnings("unchecked")
    default Requests<T> orElse(Requests<? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        return isSuccess() ? this : (Requests<T>) other;
    }

    /**
     * Or else requests.
     *
     * @param supplier the supplier
     * @return the requests
     */
    @SuppressWarnings("unchecked")
    default Requests<T> orElse(Supplier<? extends Requests<? extends T>> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return isSuccess() ? this : (Requests<T>) supplier.get();
    }

    /**
     * Gets or else get.
     *
     * @param other the other
     * @return the or else get
     */
    default T getOrElseGet(Function<? super Throwable, ? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        if (isFailure()) {
            return other.apply(getCause());
        } else {
            return get();
        }
    }

    /**
     * Or else run.
     *
     * @param action the action
     */
    default void orElseRun(Consumer<? super Throwable> action) {
        Objects.requireNonNull(action, "action is null");
        if (isFailure()) {
            action.accept(getCause());
        }
    }

    /**
     * Gets or else throw.
     *
     * @param <X>               the type parameter
     * @param exceptionProvider the exception provider
     * @return the or else throw
     * @throws X the x
     */
    default <X extends Throwable> T getOrElseThrow(Function<? super Throwable, X> exceptionProvider) throws X {
        Objects.requireNonNull(exceptionProvider, "exceptionProvider is null");
        if (isFailure()) {
            throw exceptionProvider.apply(getCause());
        } else {
            return get();
        }
    }

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    // -- CASE

    /**
     * A succeeded Request.
     *
     * @param <T> component type of this Success
     */
    final class Success<T> implements Requests<T> {

        private final T value;

        /**
         * Constructs a Success.
         *
         * @param value The value of this Success.
         */
        private Success(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getCause() {
            throw new UnsupportedOperationException("getCause on Success");
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Requests.Success && Objects.equals(value, ((Success<?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }


        @Override
        public String toString() {
            return "Success" + "(" + value + ")";
        }
    }

    /**
     * A failed Request.
     *
     * @param <T> component type of this Failure
     */
    final class Failure<T> implements Requests<T> {

        private final Throwable cause;

        /**
         * Constructs a Failure.
         *
         * @param cause A cause of type Throwable, may not be null.
         * @throws NullPointerException if {@code cause} is null
         * @throws Throwable            if the given {@code cause} is fatal, i.e. non-recoverable
         */
        private Failure(Throwable cause) {
            Objects.requireNonNull(cause, "cause is null");
            if (RequestModule.isFatal(cause)) {
                sneakyThrow(cause);
            }
            this.cause = cause;
        }

        @Override
        public T get() {
            return sneakyThrow(cause);
        }

        @Override
        public Throwable getCause() {
            return cause;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Requests.Failure && Arrays.deepEquals(cause.getStackTrace(), ((Failure<?>) obj).cause.getStackTrace()));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(cause.getStackTrace());
        }

        @Override
        public String toString() {
            return "Failure" + "(" + cause + ")";
        }
    }
}

/**
 * Don't expose this as public API
 */
interface RequestModule {

    /**
     * Is fatal boolean.
     *
     * @param throwable the throwable
     * @return the boolean
     */
    static boolean isFatal(Throwable throwable) {
        return throwable instanceof InterruptedException
                || throwable instanceof LinkageError
                || throwable instanceof ThreadDeath
                || throwable instanceof VirtualMachineError;
    }

    /**
     * Sneaky throw r.
     *
     * @param <T> the type parameter
     * @param <R> the type parameter
     * @param t   the t
     * @return the r
     * @throws T the t
     */
    @SuppressWarnings("unchecked")
    static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}