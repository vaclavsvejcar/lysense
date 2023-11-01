package com.norcane.toolkit;

import java.util.Objects;

public final class Prelude {

    private Prelude() {
        // utility class - hence the private constructor
        throw new IllegalStateException();
    }

    /**
     * Checks that the given object is not {@code null} and returns it, otherwise throws {@link NullPointerException}.
     *
     * @param object object to check for nullity
     * @param <T>    type of the object
     * @return checked object
     */
    public static <T> T nonNull(T object) {
        return Objects.requireNonNull(object);
    }

    /**
     * Returns back the given object if not {@code null}, otherwise returns the provided default object.
     *
     * @param object     object to return if not {@code null}
     * @param defaultObj default object
     * @param <T>        type of the object
     * @return provided <i>non-null</i> object or <i>default</i>
     */
    public static <T> T nonNullOrDefault(T object, T defaultObj) {
        return Objects.requireNonNullElse(object, defaultObj);
    }
}
