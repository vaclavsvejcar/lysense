package com.norcane.toolkit.state;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class MemoizedTest implements Stateful {

    @Test
    void testResetState() {
        final Memoized<String> memoized1 = Memoized.bindTo(this);
        final Memoized<String> memoized2 = Memoized.bindTo(this);

        memoized1.computeIfAbsent(() -> "value1");
        memoized2.computeIfAbsent(() -> "value2");

        assertFalse(memoized1.isEmpty());
        assertFalse(memoized2.isEmpty());

        resetState();

        assertTrue(memoized1.isEmpty());
        assertTrue(memoized2.isEmpty());
    }

    @Test
    void get() {
        @SuppressWarnings("unchecked") final Supplier<String> supplier = mock(Supplier.class);

        final String value = "Hello there!";
        final Memoized<String> memoized = Memoized.empty();

        // -- mocks
        when(supplier.get()).thenReturn(value);

        final String result1 = memoized.computeIfAbsent(supplier);
        final String result2 = memoized.computeIfAbsent(supplier);

        memoized.resetState();

        final String result3 = memoized.computeIfAbsent(supplier);

        assertEquals(value, result1);
        assertEquals(value, result2);
        assertEquals(value, result3);

        // -- verify
        verify(supplier, times(2)).get();
    }

    @Test
    void testToString() {
        final Memoized<String> memoized = Memoized.empty();
        assertEquals("Memoized{value=<absent>}", memoized.toString());

        memoized.computeIfAbsent(() -> "foo");
        assertEquals("Memoized{value=foo}", memoized.toString());
    }

    @Test
    void testGet() {
        assertFalse(Memoized.empty().get().isPresent());
    }
}