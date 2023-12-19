package com.norcane.toolkit;

import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Prelude {

    private Prelude() {
        // utility class - hence the private constructor
        throw new IllegalStateException();
    }

    /**
     * Concise alternative for single line {@code if} expression.
     *
     * <br><br><strong>Example of use</strong>
     * {@snippet lang = "java":
     *      when(fileSaved, () -> System.out.println("File successfully saved!"));
     *}
     */
    public static void when(boolean condition, Supplier<?> supplier) {
        if (condition) {
            supplier.get();
        }
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

    /**
     * Transforms the given <i>iterable</i> into a <i>map</i> using the provided key mapper function.
     *
     * @param keyMapper function that maps the value to the key
     * @param iterable  iterable to transform
     * @param <K>       type of the key
     * @param <V>       type of the value
     * @return transformed map
     */
    public static <K, V> Map<K, V> toMap(Function<V, K> keyMapper, Iterable<V> iterable) {
        return StreamSupport.stream(nonNull(iterable).spliterator(), false).collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    /**
     * Returns back the given object if not {@code null}, otherwise throws {@link IllegalArgumentException} with the provided message.
     *
     * <br><br><strong>Example of use</strong>
     * {@snippet lang = "java":
     *      final User user = nonNullOrThrow(findUser(username), "no user found for username");
     *}
     *
     * @param object           object to check for nullity and return if not {@code null}
     * @param exceptionMessage message to use in the exception if object is {@code null}
     * @param <R>              type of the object
     * @return checked object
     * @throws IllegalArgumentException when object is {@code null}
     */
    public static <R> R nonNullOrThrow(R object, String exceptionMessage) {
        if (object == null) {
            throw new IllegalArgumentException(exceptionMessage);
        }

        return object;
    }

    /**
     * Returns a sequential {@link Stream} for the provided enumeration as its source.
     *
     * @param enumeration enumeration to stream
     * @param <T>         type of the enumeration elements
     * @return stream of the enumeration
     */
    public static <T> Stream<T> streamOf(Enumeration<T> enumeration) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(enumeration.asIterator(), Spliterator.ORDERED), false);
    }
}
