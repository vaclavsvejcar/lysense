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
package com.norcane.lysense.source.metadata;

import com.norcane.lysense.source.HeaderDetectionRules;
import com.norcane.lysense.source.comment.CommentDetectorFactory;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static com.norcane.lysense.test.Assertions.assertNotPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class MetadataTest {

    static CommentDetectorFactory commentDetectorFactory = CommentDetectorFactory.blockSyntax(Pattern.compile("^/\\*(?!\\*)"), Pattern.compile("\\*/$"));
    static HeaderDetectionRules headerDetectionRules = HeaderDetectionRules.putBefore(Pattern.compile("^package"));
    static HeaderDetectionRules headerDetectionRulesPutAfter = HeaderDetectionRules.from(Pattern.compile("^PA"), Pattern.compile("^package"));

    @Test
    void header_noHeaderFound() {
        final String sample = """
            package foo;
                        
            /*
             * This is not header
             */
                        
            class Test {
                public static void main(String[] args) {
                    System.out.println("Hello!");
                }
            }
            """;

        final Metadata metadata = buildMetadata(sample);
        assertNotPresent(metadata.header());
    }

    @Test
    void header_headerFound() {
        final String sample = """
                        
            /*
             * This is header
             */
                        
                        
            package foo;
                        
            class Test {
                public static void main(String[] args) {
                    System.out.println("Hello!");
                }
            }
            """;
        final LicenseHeader expected = new LicenseHeader(2, 4, 1, 2, List.of("/*", " * This is header", " */"));

        final Metadata metadata = buildMetadata(sample);
        assertIsPresent(expected, metadata.header());
    }

    @Test
    void headerCandidate_noPutAfter_noSpaces() {
        final String sample = """
            foo bar
            """;

        final Metadata metadata = buildMetadata(sample);
        assertEquals(new HeaderCandidate(0, 0), metadata.headerCandidate());
    }

    @Test
    void headerCandidate_noPutAfter_hasSpaces() {
        final String sample = """
                        
            foo bar
            """;

        final Metadata metadata = buildMetadata(sample);
        assertEquals(new HeaderCandidate(0, 1), metadata.headerCandidate());
    }

    @Test
    void headerCandidate_putAfter_noSpaces() {
        final String sample = """
                        
            foo bar
                        
            PA
            hello
            """;

        final Metadata metadata = buildMetadataPutAfter(sample);
        assertEquals(new HeaderCandidate(4, 0), metadata.headerCandidate());
    }

    @Test
    void headerCandidate_putAfter_withSpaces() {
        final String sample = """
                        
            foo bar
                        
            PA
                        
            hello
            """;

        final Metadata metadata = buildMetadataPutAfter(sample);
        assertEquals(new HeaderCandidate(4, 1), metadata.headerCandidate());
    }

    @Test
    void putAfterIndex() {
        final String sample = """
                        
            foo
                        
            PA
                        
            PA
            """;

        final Metadata metadata = buildMetadataPutAfter(sample);
        assertIsPresent(3, metadata.putAfterIndex());
    }

    @Test
    void putBeforeIndex() {
        final String sample = """
                        
            foo
                        
            package foo;
                        
            package bar;
                        
            package baz;
            """;

        final Metadata metadata = buildMetadata(sample);
        assertIsPresent(3, metadata.putBeforeIndex());
    }

    private Metadata buildMetadata(String sample) {
        final Metadata.Builder builder = Metadata.builder(commentDetectorFactory, headerDetectionRules);
        Arrays.stream(sample.split("\n")).forEach(builder::addLine);

        return builder.build();
    }

    private Metadata buildMetadataPutAfter(String sample) {
        final Metadata.Builder builder = Metadata.builder(commentDetectorFactory, headerDetectionRulesPutAfter);
        Arrays.stream(sample.split("\n")).forEach(builder::addLine);

        return builder.build();
    }
}
