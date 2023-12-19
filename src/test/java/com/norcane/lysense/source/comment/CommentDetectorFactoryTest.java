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
package com.norcane.lysense.source.comment;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class CommentDetectorFactoryTest {

    static final Pattern PATTERN_BLOCK_START = Pattern.compile("^/\\*");
    static final Pattern PATTERN_BLOCK_END = Pattern.compile("\\*/$");
    static final Pattern PATTERN_LINE = Pattern.compile("^//");

    @Test
    void isHeader_block_multiLine() {
        final CommentDetector detector = CommentDetectorFactory.blockSyntax(PATTERN_BLOCK_START, PATTERN_BLOCK_END).create();

        assertFalse(detector.isComment(""));
        assertFalse(detector.isComment("functionCall();"));
        assertTrue(detector.isComment("/* header starts"));
        assertTrue(detector.isComment("header body"));
        assertTrue(detector.isComment("header ends */"));
        assertFalse(detector.isComment("after header"));
        assertTrue(detector.isComment("/* block comment but not header */"));
    }

    @Test
    void isHeader_block_singleLine() {
        final CommentDetector detector = CommentDetectorFactory.blockSyntax(PATTERN_BLOCK_START, PATTERN_BLOCK_END).create();

        assertFalse(detector.isComment(""));
        assertFalse(detector.isComment("functionCall();"));
        assertTrue(detector.isComment("/* single line block header */"));
        assertFalse(detector.isComment("after header"));
        assertTrue(detector.isComment("/* block comment but not header */"));
    }

    @Test
    void isHeader_line() {
        final CommentDetector detector = CommentDetectorFactory.lineSyntax(PATTERN_LINE).create();

        assertFalse(detector.isComment("some.code();"));
        assertFalse(detector.isComment("some // line comment but not header"));
        assertTrue(detector.isComment("// line header starts"));
        assertTrue(detector.isComment("// continues here"));
        assertTrue(detector.isComment("// and ends here"));
        assertFalse(detector.isComment("some other code"));
        assertTrue(detector.isComment("// line comment but not header"));
    }

}
