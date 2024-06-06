package spelbergit.monad;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed abstract class Try<T, E extends Throwable> {

    public static <T, E extends Throwable> Try<T, E> of(TrySupplier<T, E> trySupplier) {
        try {
            return new Value<>(trySupplier.get());
        } catch (Throwable e) {
            @SuppressWarnings("unchecked") E cause = (E) e;
            return new Error<>(cause);
        }
    }

    public static <T, E extends Throwable> Try<T, E> value(T value) {
        return new Value<>(value);
    }

    public static <T, E extends Throwable> Try<T, E> error(E cause) {
        return new Error<>(cause);
    }

    @FunctionalInterface public interface TrySupplier<T, E extends Throwable> {
        T get() throws E;
    }

    @FunctionalInterface public interface TryFunction<T, R, E extends Throwable> {
        R apply(T value) throws E;
    }

    private static final class Value<T, E extends Throwable> extends Try<T, E> {
        private final T value;

        public Value(T value) {
            this.value = value;
        }

        @Override public T getOrElse(T other) {
            return value;
        }

        @Override public T getOrElse(Supplier<T> otherSupplier) {
            return value;
        }

        @Override public <F extends Throwable> Try<T, F> getOrElseTry(TrySupplier<T, F> otherSupplier) {
            return new Value<>(value);
        }

        @Override public T getOrThrow() {
            return value;
        }

        @Override public <R> Try<R, E> map(TryFunction<? super T, R, ? extends E> transform) {
            return Try.of(() -> transform.apply(value));
        }

        @Override public <F extends Throwable> Try<T, F> mapError(Function<? super E, ? extends F> transformCause) {
            return new Value<>(value);
        }

        @Override public Stream<T> streamOrThrow() {
            return Stream.of(value);
        }

        @Override public Optional<T> optionalOrThrow() {
            return Optional.ofNullable(value);
        }
    }

    private static final class Error<T, E extends Throwable> extends Try<T, E> {

        private final E cause;

        public Error(E cause) {
            this.cause = cause;
        }

        @Override public T getOrElse(T other) {
            return other;
        }

        @Override public T getOrElse(Supplier<T> otherSupplier) {
            return otherSupplier.get();
        }

        @Override public <F extends Throwable> Try<T, F> getOrElseTry(TrySupplier<T, F> otherSupplier) {
            return Try.of(otherSupplier);
        }

        @Override public T getOrThrow() throws E {
            throw cause;
        }

        @Override public <R> Try<R, E> map(TryFunction<? super T, R, ? extends E> transform) {
            return new Error<>(cause);
        }

        @Override public <F extends Throwable> Try<T, F> mapError(Function<? super E, ? extends F> transformCause) {
            return new Error<>(transformCause.apply(cause));
        }

        @Override public Stream<T> streamOrThrow() throws E {
            throw cause;
        }

        @Override public Optional<T> optionalOrThrow() throws E {
            throw cause;
        }
    }

    public abstract T getOrElse(T other);

    public abstract T getOrElse(Supplier<T> otherSupplier);

    public abstract <F extends Throwable> Try<T, F> getOrElseTry(TrySupplier<T, F> otherSupplier);

    public abstract T getOrThrow() throws E;

    public abstract <R> Try<R, E> map(TryFunction<? super T, R, ? extends E> transform);

    public abstract <F extends Throwable> Try<T, F> mapError(Function<? super E, ? extends F> transformCause);

    public abstract Stream<T> streamOrThrow() throws E;

    public abstract Optional<T> optionalOrThrow() throws E;
}
