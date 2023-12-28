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

public class SetupContext {

    private final Map<Object, Object> values = new HashMap<>();

    public <V> void put(SetupContextKey<V> key, V value) {
        values.put(key, key.valueClass().cast(value));
    }

    public <V, C extends Collection<V>> void put(CollectionSetupContextKey<C, V> key, C value) {
        values.put(key, value);
    }

    public <V> V get(SetupContextKey<V> key) {
        final Object value = values.get(key);

        if (value == null) {
            throw new IllegalArgumentException(STR."No value found for key: \{key}");
        }

        return key.valueClass().cast(value);
    }

    @SuppressWarnings("unchecked")
    public <V, C extends Collection<V>> C get(CollectionSetupContextKey<C, V> key) {
        final Object value = values.get(key);

        if (value == null) {
            throw new IllegalArgumentException(STR."No value found for key: \{key}");
        }

        return (C) value;
    }
}
