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

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SetupContextTest {

    @Test
    void get() {
        final SetupContext context = new SetupContext(Map.of("foo", 42));

        assertThrows(IllegalArgumentException.class, () -> context.get("bar", Integer.class));
        assertEquals(42, context.get("foo", Integer.class));
    }

    @Test
    void getList() {
        final SetupContext context = new SetupContext();

        context.put("foo", List.of(1, 2, 3));

        assertThrows(IllegalArgumentException.class, () -> context.get("bar", Integer.class));
        assertThrows(IllegalArgumentException.class, () -> context.getList("foo", String.class));

        assertEquals(List.of(1, 2, 3), context.getList("foo", Integer.class));
    }

    @Test
    void getSet() {
        final SetupContext context = new SetupContext();

        context.put("foo", Set.of(1, 2, 3));

        assertThrows(IllegalArgumentException.class, () -> context.get("bar", Integer.class));
        assertThrows(IllegalArgumentException.class, () -> context.getSet("foo", String.class));

        assertEquals(Set.of(1, 2, 3), context.getSet("foo", Integer.class));
    }

    @Test
    void testEquals() {
        final SetupContext context1 = new SetupContext(Map.of("foo", 42));
        final SetupContext context2 = new SetupContext(Map.of("foo", 42));
        final SetupContext context3 = new SetupContext(Map.of("foo", 43));

        assertEquals(context1, context2);
        assertNotEquals(context1, context3);
    }

    @Test
    void testHashCode() {
        final SetupContext context1 = new SetupContext(Map.of("foo", 42));
        final SetupContext context2 = new SetupContext(Map.of("foo", 42));
        final SetupContext context3 = new SetupContext(Map.of("foo", 43));

        assertEquals(context1.hashCode(), context2.hashCode());
        assertNotEquals(context1.hashCode(), context3.hashCode());
    }

    @Test
    void testToString() {
        final SetupContext context = new SetupContext(Map.of("foo", 42));

        assertEquals("SetupContext{context={foo=42}}", context.toString());
    }
}
