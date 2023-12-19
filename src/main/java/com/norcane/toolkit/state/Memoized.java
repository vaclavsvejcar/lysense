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
package com.norcane.toolkit.state;

import com.google.common.base.MoreObjects;

import java.util.Optional;
import java.util.function.Supplier;

import static com.norcane.toolkit.Prelude.nonNullOrDefault;

/**
 * Represents variable which value is computed only once using given supplier and then memoized.
 *
 * @param <T> type of the memoized value
 */
public final class Memoized<T> implements Stateful {

    private T value;

    private Memoized(T value) {
        this.value = value;
    }

    /**
     * Constructs new instance with no memoized value that is not registered to any <i>stateful</i> parent.
     *
     * @param <T> type of the memoized value
     * @return new instance
     */
    public static <T> Memoized<T> detached() {
        return new Memoized<>(null);
    }

    /**
     * Constructs new instance with no memoized value and registers it to given <i>stateful</i> parent, so when the parent state is reset, the memoized value is
     * reset as well.
     *
     * @param parent parent stateful object
     * @param <T>    type of the memoized value
     * @return new instance
     */
    public static <T> Memoized<T> bindTo(Stateful parent) {
        final Memoized<T> memoized = new Memoized<>(null);

        parent.stateContext().register(memoized);
        return memoized;
    }

    /**
     * Returns the memoized value and if no value has been computed yet, uses given supplier to compute and store the value.
     *
     * @param supplier supplier used to compute the value
     * @return value
     */
    public T computeIfAbsent(Supplier<T> supplier) {
        return value == null
               ? (value = supplier.get())
               : value;
    }

    /**
     * Returns the memoized value (if computed) or empty optional.
     *
     * @return memoized value
     */
    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    /**
     * Returns whether if the memoized value is present
     *
     * @return {@code true} if the memoized value is present
     */
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public void resetState() {
        value = null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("value", nonNullOrDefault(value, "<absent>"))
            .toString();
    }
}
