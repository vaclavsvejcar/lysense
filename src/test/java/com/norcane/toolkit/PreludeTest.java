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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertNonInstantiable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class PreludeTest {

    @Test
    void when() {
        final AtomicInteger counter = new AtomicInteger(0);

        Prelude.when(true, counter::incrementAndGet);
        Prelude.when(false, counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

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
    void notNullOrThrow() {
        assertEquals(42, Prelude.nonNullOrThrow(42, "foo"));
        assertThrows(IllegalArgumentException.class, () -> Prelude.nonNullOrThrow(null, "foo"));
        assertThrows(IllegalStateException.class, () -> Prelude.nonNullOrThrow(null, IllegalStateException::new));
    }

    @Test
    void toMap() {
        assertEquals(Map.of("1", 1, "2", 2), Prelude.toMap(String::valueOf, List.of(1, 2)));
    }

    @Test
    void streamOf() {
        final List<String> list = List.of("one", "two");

        assertEquals(list, Prelude.streamOf(Collections.enumeration(list)).toList());
    }
}
