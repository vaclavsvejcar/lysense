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
package com.norcane.lysense.cli.command.setup.step;

import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Mutable context for setup steps.
 */
public class SetupContext {

    private final Map<String, Object> context;

    /**
     * Creates empty context.
     */
    public SetupContext() {
        this(new HashMap<>());
    }

    /**
     * Creates context with given values.
     *
     * @param context initial values
     */
    public SetupContext(Map<String, Object> context) {
        this.context = new HashMap<>(context);
    }

    /**
     * Returns value for given key.
     *
     * @param key  key
     * @param type value type class
     * @param <T>  value type
     * @return value
     * @throws IllegalArgumentException if no value found for given key
     */
    public <T> T get(String key, Class<T> type) {
        final Object value = context.get(key);

        if (value == null) {
            throw new IllegalArgumentException(STR."No value found for key: \{key}");
        }

        return type.cast(value);
    }

    /**
     * Returns value of type {@link List} for given key.
     *
     * @param key         key
     * @param elementType element type class
     * @param <T>         element type
     * @return list of values
     * @throws IllegalArgumentException if no value found for given key
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> elementType) {
        final Object value = context.get(key);

        if (value == null) {
            throw new IllegalArgumentException(STR."No value found for key: \{key}");
        }

        final List<?> list = (List<?>) value;
        if (!list.isEmpty() && !elementType.isInstance(list.getFirst())) {
            throw new IllegalArgumentException(STR."Expected list of \{elementType} but got: \{list}");
        }


        return (List<T>) value;
    }

    /**
     * Puts value into context.
     *
     * @param key   key
     * @param value value
     * @param <T>   value type
     */
    public <T> void put(String key, T value) {
        context.put(nonNull(key), nonNull(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SetupContext that = (SetupContext) o;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("context", context)
                .toString();
    }
}
