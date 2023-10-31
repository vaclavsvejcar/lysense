package com.norcane.lysense.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class Assertions {

    private Assertions() {
        // utility class - hence the private constructor
        throw new IllegalStateException();
    }

    public static <S> void assertNonInstantiable(Class<S> singletonClass) {

        final Constructor<S> constructor;

        try {
            constructor = singletonClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        constructor.setAccessible(true);

        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> void assertIsPresent(T expected, Optional<T> actual) {
        assertNotNull(actual);
        assertEquals(Optional.of(expected), actual);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> void assertNotPresent(Optional<T> actual) {
        assertNotNull(actual);
        assertEquals(Optional.empty(), actual);
    }

    public static <T> void assertInstance(Class<T> theClass, T expected, Object actual) {
        assertTrue(theClass.isInstance(actual));
        assertEquals(expected, theClass.cast(actual));
    }
}
