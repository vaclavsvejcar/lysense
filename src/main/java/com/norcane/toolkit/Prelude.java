/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
     *      final User theUser = nonNullOrThrow(findUser(username), "no user found for username");
     *}
     *
     * @param object                          object to check for nullity and return if not {@code null}
     * @param illegalArgumentExceptionMessage message to use in the exception if object is {@code null}
     * @param <R>                             type of the object
     * @return checked object
     * @throws IllegalArgumentException when object is {@code null}
     */
    public static <R> R nonNullOrThrow(R object, String illegalArgumentExceptionMessage) {
        return nonNullOrThrow(object, () -> new IllegalArgumentException(illegalArgumentExceptionMessage));
    }

    /**
     * Returns back the given object if not {@code null}, otherwise throws {@link RuntimeException} provided by the given supplier.
     *
     * @param object           object to check for nullity and return if not {@code null}
     * @param runtimeException supplier of the exception to throw if object is {@code null}
     * @param <R>              type of the object
     * @return checked object
     * @throws RuntimeException when object is {@code null}
     */
    public static <R> R nonNullOrThrow(R object, Supplier<RuntimeException> runtimeException) {
        if (object == null) {
            throw runtimeException.get();
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
