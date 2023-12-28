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
package com.norcane.lysense.cli.command.setup.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a mutable context for setup steps. Uses {@link SetupContextKey} and {@link CollectionSetupContextKey} as a
 * type-safe keys that also defines the type of the value for given key.
 *
 * @see SetupContextKey
 * @see CollectionSetupContextKey
 */
public class SetupContext {

    private final Map<Object, Object> values = new HashMap<>();

    /**
     * Puts the given {@code value} into the context under the given {@code key}.
     *
     * @param key   key to be used for storing the value
     * @param value value to be stored
     * @param <V>   type of the value
     */
    public <V> void put(SetupContextKey<V> key, V value) {
        values.put(key, key.valueClass().cast(value));
    }

    /**
     * Puts the given collection {@code value} into the context under the given {@code key}.
     *
     * @param key   key to be used for storing the value
     * @param value collection to be stored
     * @param <V>   type of the collection value
     * @param <C>   type of the collection
     */
    public <V, C extends Collection<V>> void put(CollectionSetupContextKey<C, V> key, C value) {
        values.put(key, value);
    }

    /**
     * Returns the value stored under the given {@code key}.
     *
     * @param key key to be used for retrieving the value
     * @param <V> type of the value
     * @return value stored under the given {@code key}
     * @throws IllegalArgumentException if no value is found for given {@code key}
     */
    public <V> V get(SetupContextKey<V> key) {
        final Object value = values.get(key);

        if (value == null) {
            throw new IllegalArgumentException(STR."No value found for key: \{key}");
        }

        return key.valueClass().cast(value);
    }

    /**
     * Returns the collection value stored under the given {@code key}.
     *
     * @param key key to be used for retrieving the value
     * @param <V> type of the collection value
     * @param <C> type of the collection
     * @return collection value stored under the given {@code key}
     * @throws IllegalArgumentException if no value is found for given {@code key}
     */
    @SuppressWarnings("unchecked")
    public <V, C extends Collection<V>> C get(CollectionSetupContextKey<C, V> key) {
        final Object value = values.get(key);

        if (value == null) {
            throw new IllegalArgumentException(STR."No value found for key: \{key}");
        }

        return (C) value;
    }
}
