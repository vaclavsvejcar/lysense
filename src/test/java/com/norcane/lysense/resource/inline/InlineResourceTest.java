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
package com.norcane.lysense.resource.inline;

import com.norcane.lysense.resource.util.LineSeparator;
import com.norcane.toolkit.net.URIs;

import org.junit.jupiter.api.Test;

import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class InlineResourceTest {

    @Test
    void of_uri() {
        final URI uri = URI.create("inline:java;name=foo/hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh");
        final InlineResource resource = InlineResource.of(uri);

        assertEquals("java", resource.extension());
        assertEquals("hello world", resource.name());
        assertIsPresent("foo", resource.parent());
        assertEquals("The Cake is a Lie!", resource.readAsString());
        assertEquals(uri, resource.uri());
        assertEquals(LineSeparator.platform(), resource.lineSeparator());
    }

    @Test
    void of_uri_invalidScheme() {
        assertThrows(IllegalArgumentException.class, () -> InlineResource.of(URIs.create("file:///foo/bar.txt")));
    }

    @Test
    void of_params() {
        final URI uri = URIs.create("inline:java;name=/foo/hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh");
        final InlineResource resource = InlineResource.of("/foo/hello world", "java", "The Cake is a Lie!");

        assertEquals("java", resource.extension());
        assertEquals("hello world", resource.name());
        assertIsPresent("/foo", resource.parent());
        assertEquals("The Cake is a Lie!", resource.readAsString());
        assertEquals(uri, resource.uri());
    }
}
