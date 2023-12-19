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
        final Memoized<String> memoized = Memoized.detached();

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
        final Memoized<String> memoized = Memoized.detached();
        assertEquals("Memoized{value=<absent>}", memoized.toString());

        memoized.computeIfAbsent(() -> "foo");
        assertEquals("Memoized{value=foo}", memoized.toString());
    }

    @Test
    void testGet() {
        assertFalse(Memoized.detached().get().isPresent());
    }
}
