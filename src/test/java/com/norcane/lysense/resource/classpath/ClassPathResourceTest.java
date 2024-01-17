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
package com.norcane.lysense.resource.classpath;

import com.google.common.io.CharStreams;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.lysense.resource.util.LineSeparator;
import com.norcane.toolkit.net.URIs;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class ClassPathResourceTest {

    static final String content = "Hello\nthere!";
    static final URI uri = URIs.create("classpath:/classpath-resource.txt");
    static final Resource resource = ClassPathResource.of("/classpath-resource.txt");

    @Test
    void of() {
        final ClassPathResource classPathResource1 = ClassPathResource.of(URI.create("classpath:/classpath-resource.txt"));
        final ClassPathResource classPathResource2 = ClassPathResource.of("/classpath-resource.txt");
        assertEquals(classPathResource1, classPathResource2);
    }

    @Test
    void name() {
        assertEquals("classpath-resource", resource.name());
    }

    @Test
    void extension() {
        assertEquals("txt", resource.extension());
    }

    @Test
    void parent() {
        assertIsPresent("/", resource.parent());
        assertIsPresent("/resources-test", ClassPathResource.of("/resources-test/a.txt").parent());
    }

    @Test
    void uri() {
        assertEquals(uri, resource.uri());
    }

    @Test
    void reader() throws Exception {
        try (final Reader reader = resource.reader()) {
            assertEquals(content, CharStreams.toString(reader));
        }
    }

    @Test
    void readAsString() {
        assertEquals(content, resource.readAsString());
        assertThrows(ResourceNotFoundException.class, () -> ClassPathResource.of(URIs.create("classpath:/not/existing")).readAsString());
    }

    @Test
    void lineSeparator() {
        assertEquals(LineSeparator.LF, resource.lineSeparator());
    }

}
