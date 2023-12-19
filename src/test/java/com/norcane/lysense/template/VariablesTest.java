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
package com.norcane.lysense.template;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class VariablesTest {

    @Test
    void from_listOfVariable() {
        final Variables variables = Variables.from(List.of(new Variables.Variable("foo", "bar")));

        assertEquals(Map.of("foo", "bar"), variables.toMap());
    }

    @Test
    void toMap() {
        final Map<String, Object> raw = Map.of("one", 1, "two", 2);
        final Variables variables = Variables.from(raw);

        assertEquals(raw, variables.toMap());
    }

    @Test
    void isEmpty() {
        assertTrue(Variables.empty().isEmpty());
        assertFalse(Variables.from(Map.of("one", 1)).isEmpty());
    }

    @Test
    void size() {
        assertEquals(0, Variables.empty().size());
        assertEquals(1, Variables.from(Map.of("one", 1)).size());
    }

    @Test
    void testEquals() {
        assertEquals(Variables.empty(), Variables.empty());
        assertNotEquals(Variables.empty(), "other type");
        assertNotEquals(null, Variables.from(Map.of()));
    }

    @Test
    void mergeWith() {
        final Variables variables1 = Variables.from(Map.of("one", 1, "two", 2));
        final Variables variables2 = Variables.from(Map.of("two", "zwei", "three", 3));
        final Variables expected = Variables.from(Map.of("one", 1, "two", "zwei", "three", 3));

        assertEquals(expected, variables1.mergeWith(variables2));
    }

    @Test
    void testHashCode() {
        final Variables variables1 = Variables.from(Map.of("one", 1));
        final Variables variables2 = Variables.from(Map.of("two", 2));

        assertEquals(variables1.hashCode(), variables1.hashCode());
        assertNotEquals(variables1.hashCode(), variables2.hashCode());
    }

    @Test
    void testToString() {
        assertNotNull(Variables.from(Map.of("one", 1)).toString());
    }
}
