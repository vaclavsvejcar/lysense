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
package com.norcane.lysense.meta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class SemVerTest {

    @Test
    void from_illegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> SemVer.from("foo"));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "0.1.0    |0|1|0|",
            "0.1.0-RC3|0|1|0|RC3"
        })
    void from(String raw, int major, int minor, int patch, String suffix) {
        assertEquals(new SemVer(major, minor, patch, suffix), SemVer.from(raw));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "true |0.1.0|0.1.1",
            "false|0.1.1|0.1.0",
            "false|0.1.1|0.1.1"
        }
    )
    void isLowerThan(boolean result, String left, String right) {
        assertEquals(result, SemVer.from(left).isLowerThan(SemVer.from(right)));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "false|0.1.0|0.1.1",
            "true |0.1.1|0.1.0",
            "false|0.1.1|0.1.1"
        }
    )
    void isGreaterThan(boolean result, String left, String right) {
        assertEquals(result, SemVer.from(left).isGreaterThan(SemVer.from(right)));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            " 0|0.1.0    |0.1.0",
            " 1|0.1.0    |0.1.0-RC3",
            "-1|0.1.0-RC2|0.1.0-RC3",
            "-1|0.1.0    |0.1.1",
            " 1|1.0.1    |0.2.1",
            " 1|1.1.0    |1.0.0",
            "-1|1.0.0-RC1|1.0.0",
            " 1|1.0.0-2  |1.0.0-1"
        })
    void compareTo(int result, String left, String right) {
        assertEquals(result, SemVer.from(left).compareTo(SemVer.from(right)));
    }

    @ParameterizedTest
    @CsvSource({"0.1.0", "0.1.0-RC3"})
    void testToString(String raw) {
        assertEquals(raw, SemVer.from(raw).toString());
    }
}
