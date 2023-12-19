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
package com.norcane.lysense.resource.util;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.inline.InlineResource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static com.norcane.lysense.test.Assertions.assertNotPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LineSeparatorTest {

    @Test
    void from() {
        assertIsPresent(LineSeparator.CR, LineSeparator.from("\r"));
        assertIsPresent(LineSeparator.LF, LineSeparator.from("\n"));
        assertIsPresent(LineSeparator.CRLF, LineSeparator.from("\r\n"));
        assertNotPresent(LineSeparator.from("foo"));
    }

    @ParameterizedTest
    @MethodSource("detectArguments")
    void detect(Resource resource, LineSeparator expected) {
        assertEquals(expected, LineSeparator.detect(resource).orElse(null));
    }

    private static Stream<Arguments> detectArguments() {
        return Stream.of(
            Arguments.of(InlineResource.of("Hello, world!"), null),
            Arguments.of(InlineResource.of("one\rtwo\rthree"), LineSeparator.CR),
            Arguments.of(InlineResource.of("one\ntwo\nthree"), LineSeparator.LF),
            Arguments.of(InlineResource.of("one\r\ntwo\r\nthree"), LineSeparator.CRLF)
        );
    }

}
