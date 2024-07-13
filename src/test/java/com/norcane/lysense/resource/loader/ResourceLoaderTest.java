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
package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ResourceLoaderTest {

    @Inject
    ResourceLoader resourceLoader;

    @Test
    void resource() {
        // check that concrete resource is loaded
        final Resource resource = resourceLoader.resource("classpath:/classpath-resource.txt");
        assertEquals(URI.create("classpath:/classpath-resource.txt"), resource.uri());

        // check that exception is thrown for non-existent resource
        assertThrows(ResourceNotFoundException.class, () -> resourceLoader.resource("/foo/bar.txt"));

        // check that exception is thrown for non-file resource
        assertThrows(ResourceNotFoundException.class, () -> resourceLoader.resource("/resources-test"));
    }

    @Test
    void resources() throws IOException {
        final Path tempDirectory = Files.createTempDirectory(null);
        final Path fileA = Path.of("a.txt");
        final Path fileB = Path.of("foo/b.txt");
        Files.createDirectories(tempDirectory.resolve(fileB.getParent()));
        Files.createFile(tempDirectory.resolve(fileA));
        Files.createFile(tempDirectory.resolve(fileB));

        // check that all resources from given directory are recursively loaded
        final List<Resource> resources1 = resourceLoader.resources(tempDirectory.toString(), _ -> true, true);
        assertEquals(2, resources1.size());
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check that all resources from given directory are recursively loaded when using '**' GLOB
        final List<Resource> resources2 = resourceLoader.resources(tempDirectory + "/**", _ -> true, true);
        assertEquals(2, resources2.size());
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check that all resources from given directory are non-recursively loaded
        final List<Resource> resources3 = resourceLoader.resources(tempDirectory.toString(), _ -> true, false);
        assertEquals(1, resources3.size());
        assertTrue(resources3.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));

        // check that concrete resource is returned when using absolute path
        final List<Resource> resources4 = resourceLoader.resources(tempDirectory.resolve(fileA).toString(), _ -> true, true);
        assertEquals(1, resources4.size());
        assertTrue(resources4.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
    }
}
