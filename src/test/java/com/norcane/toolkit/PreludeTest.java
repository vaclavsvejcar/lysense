package com.norcane.toolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertNonInstantiable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class PreludeTest {

    @Test
    void testNonInstantiable() {
        assertNonInstantiable(Prelude.class);
    }

    @Test
    void nonNull() {
        assertEquals("42", Prelude.nonNull("42"));
        assertThrows(NullPointerException.class, () -> Prelude.nonNull(null));
    }

    @ParameterizedTest
    @CsvSource({"42,42,84", "84,,84"})
    void nonNullOrDefault(String expected, String input, String defaultValue) {
        assertEquals(expected, Prelude.nonNullOrDefault(input, defaultValue));
    }

    @Test
    void toMap() {
        assertEquals(Map.of("1", 1, "2", 2), Prelude.toMap(String::valueOf, List.of(1, 2)));
    }
}