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
package com.norcane.lysense.splicer.weaving;

import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.splicer.Operation;
import com.norcane.lysense.test.InMemoryWritableResourceWrapper;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class WeavingResourceSplicerTest {


    @Inject
    WeavingResourceSplicer splicer;

    @Test
    void splice_addSection_firstLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.addSection(1, "INS"));

        assertEquals("INS\none\ntwo\nthree\n", resource.writtenString());
    }

    @Test
    void splice_addSection_singleLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.addSection(2, "INS"));

        assertEquals("one\nINS\ntwo\nthree\n", resource.writtenString());
    }

    @Test
    void splice_addSection_moreLines() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.addSection(2, "FOO\nBAR"));

        assertEquals("one\nFOO\nBAR\ntwo\nthree\n", resource.writtenString());
    }

    @Test
    void splice_dropSection_singleLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.dropSection(2, 2));

        assertEquals("one\nthree\n", resource.writtenString());
    }

    @Test
    void splice_dropSection_multiLines() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\nfour\n"));
        splicer.splice(resource, Operation.dropSection(2, 3));

        assertEquals("one\nfour\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_singleLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.replaceSection(2, 2, "2"));

        assertEquals("one\n2\nthree\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_singleToMulti() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.replaceSection(2, 2, "2.1\n2.2"));

        assertEquals("one\n2.1\n2.2\nthree\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_multiToMulti() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\nfour\n"));
        splicer.splice(resource, Operation.replaceSection(2, 3, "2\n3"));

        assertEquals("one\n2\n3\nfour\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_multiToSingle() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\nfour\n"));
        splicer.splice(resource, Operation.replaceSection(2, 3, "2and3"));

        assertEquals("one\n2and3\nfour\n", resource.writtenString());
    }
}
