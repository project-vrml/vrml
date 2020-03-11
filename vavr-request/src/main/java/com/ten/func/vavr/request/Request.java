package com.ten.func.vavr.request;

import io.vavr.CheckedRunnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.ten.func.vavr.request.RequestModule.sneakyThrow;

public interface Request<T> {

    public static void main(String[] args) {
        Request<Object> check = Request.of(() -> {
            return null;
        }).check(o -> {
            if (o.equals("1")) {
                throw new RuntimeException();
            }
        });
    }

    /**
     * Creates a Request of a value.
     *
     * @param value A value
     * @param <T>   Component type
     * @return {@code Success(value)}
     */
    static <T> Request<T> of(T value) {
        Objects.requireNonNull(value, "value is null");
        return new Success<>(value);
    }

    /**
     * Creates a Request of a Supplier.
     *
     * @param supplier A supplier
     * @param <T>      Component type
     * @return {@code Success(supplier.get())} if no exception occurs, otherwise {@code Failure(throwable)} if an
     * exception occurs calling {@code supplier.get()}.
     */
    static <T> Request<T> of(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Creates a Request of a CheckedRunnable.
     *
     * @param runnable A checked runnable
     * @return {@code Success(null)} if no exception occurs, otherwise {@code Failure(throwable)} if an exception occurs
     * calling {@code runnable.run()}.
     */
    static Request<Void> of(CheckedRunnable runnable) {
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
     * }</pre>
     *
     * @param action An exception consumer
     * @return this
     * @throws NullPointerException if {@code action} is null
     */
    default Request<T> onFailure(Consumer<? super Throwable> action) {
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
     * }</pre>
     *
     * @param action A value consumer
     * @return this
     * @throws NullPointerException if {@code action} is null
     */
    default Request<T> onSuccess(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess()) {
            action.accept(get());
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    default Request<T> orElse(Request<? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        return isSuccess() ? this : (Request<T>) other;
    }

    @SuppressWarnings("unchecked")
    default Request<T> orElse(Supplier<? extends Request<? extends T>> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return isSuccess() ? this : (Request<T>) supplier.get();
    }

    default T getOrElseGet(Function<? super Throwable, ? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        if (isFailure()) {
            return other.apply(getCause());
        } else {
            return get();
        }
    }

    default void orElseRun(Consumer<? super Throwable> action) {
        Objects.requireNonNull(action, "action is null");
        if (isFailure()) {
            action.accept(getCause());
        }
    }

    default <X extends Throwable> T getOrElseThrow(Function<? super Throwable, X> exceptionProvider) throws X {
        Objects.requireNonNull(exceptionProvider, "exceptionProvider is null");
        if (isFailure()) {
            throw exceptionProvider.apply(getCause());
        } else {
            return get();
        }
    }

    /**
     * Applies the action to the value of a Success or does nothing in the case of a Failure.
     *
     * @param action A Consumer
     * @return this {@code Request}
     * @throws NullPointerException if {@code action} is null
     */
    default Request<T> peek(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess()) {
            action.accept(get());
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
     * }</pre>
     *
     * @param f A recovery function taking a Throwable
     * @return a {@code Request}
     * @throws NullPointerException if {@code f} is null
     */
    default Request<T> recover(Function<? super Throwable, ? extends T> f) {
        Objects.requireNonNull(f, "f is null");
        if (isFailure()) {
            return Request.of(() -> f.apply(getCause()));
        } else {
            return this;
        }
    }

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    /**
     * Applies the action to the value of a Success or does nothing in the case of a Failure.
     *
     * @param check A Consumer check
     * @return this {@code Request}
     * @throws NullPointerException if {@code check} is null
     */
    default Request<T> check(Consumer<? super T> check) {
        Objects.requireNonNull(check, "check is null");
        if (isSuccess()) {
            return of(() -> {
                check.accept(get());
                return get();
            });
        }
        return this;
    }

    /**
     * A succeeded Request.
     *
     * @param <T> component type of this Success
     */
    final class Success<T> implements Request<T> {

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
            return (obj == this) || (obj instanceof Request.Success && Objects.equals(value, ((Success<?>) obj).value));
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
    final class Failure<T> implements Request<T> {

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
            return (obj == this) || (obj instanceof Request.Failure && Arrays.deepEquals(cause.getStackTrace(), ((Failure<?>) obj).cause.getStackTrace()));
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

    static boolean isFatal(Throwable throwable) {
        return throwable instanceof InterruptedException
                || throwable instanceof LinkageError
                || throwable instanceof ThreadDeath
                || throwable instanceof VirtualMachineError;
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}
