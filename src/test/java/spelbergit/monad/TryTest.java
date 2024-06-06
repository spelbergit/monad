package spelbergit.monad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TryTest {
    @Test
    void tryOk() {
        assertEquals(42, Try.of(() -> 42).getOrThrow());
    }

    @Test
    void tryOrElseOk() {
        assertEquals(42, Try.of(() -> 42).getOrElse(54));
    }

    @Test
    void tryMapOk() {
        assertEquals("2a", Try.of(() -> 42).map(Integer::toHexString).getOrElse("54"));
    }

    @Test
    void tryMapNok() {
        assertEquals("not 42", assertThrows(IllegalArgumentException.class, () -> Try.of(() -> 42).map(ignored -> {
            throw new IllegalArgumentException("not 42");
        }).getOrThrow()).getMessage());
    }

    @Test
    void tryMapNokOrElse() {
        assertEquals(54, Try.of(() -> 42).map(ignored -> {
            throw new IllegalArgumentException("not 42");
        }).getOrElse(54));
    }

    @Test
    void tryOrElseNok() {
        assertEquals(54, Try.of(() -> {
            throw new IllegalArgumentException("42");
        }).getOrElse(54));
    }

    @Test
    void tryOrElseSupplyNok() {
        assertEquals(54, Try.of(() -> {
            throw new IllegalArgumentException("42");
        }).getOrElse(() -> 54));
    }

    @Test
    void tryOrElseTryOk() {
        assertEquals(54, Try.of(() -> Integer.parseInt("forty two")).getOrElseTry(() -> 54).getOrThrow());
    }

    @Test
    void tryOrElseTryNok() {
        assertEquals("test 2 OK", assertThrows(IllegalStateException.class, () -> Try.of(() -> {
            throw new ArithmeticException("test OK");
        }).getOrElseTry(() -> {
            throw new IllegalStateException("test 2 OK");
        }).getOrThrow()).getMessage());
    }

    @Test
    void tryThrow() {
        assertEquals("test OK", assertThrows(ArithmeticException.class, () -> Try.of(() -> {
            throw new ArithmeticException("test OK");
        }).getOrThrow()).getMessage());
    }

    @Test
    void tryMapErrorThrow() {
        assertEquals("wrap: test OK", assertThrows(ArrayIndexOutOfBoundsException.class, () -> Try.of(() -> {
            throw new ArithmeticException("test OK");
        }).mapError(e -> new ArrayIndexOutOfBoundsException("wrap: " + e.getMessage())).getOrThrow()).getMessage());
    }

    @Test
    void tryOptionalOk() {
        assertEquals(42, Try.of(() -> 42).optionalOrThrow().orElseThrow());
    }

    @Test
    void tryOptionalThrow() {
        assertEquals("test OK", assertThrows(ArithmeticException.class, () -> Try.of(() -> {
            throw new ArithmeticException("test OK");
        }).optionalOrThrow()).getMessage());
    }

    @Test
    void tryStreamOk() {
        assertEquals(42, Try.of(() -> 42).streamOrThrow().findFirst().orElseThrow());
    }


    @Test
    void tryStreamThrow() {
        assertEquals("test OK", assertThrows(ArithmeticException.class, () -> Try.of(() -> {
            throw new ArithmeticException("test OK");
        }).streamOrThrow()).getMessage());
    }


}