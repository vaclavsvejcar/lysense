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
package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.DefaultResourceFactory;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FileSystemResourceFactoryTest {

    @Inject
    @DefaultResourceFactory
    FileSystemResourceFactory factory;

    @Test
    void scheme() {
        assertEquals(FileSystemResource.SCHEME, factory.scheme());
    }

    @Test
    public void resource() throws Exception {
        final String content = "Hello, there!";
        final Path tempFile = Files.createTempFile(null, null);
        Files.writeString(tempFile, content);

        final URI uri = tempFile.toUri();
        final Resource resource = factory.resource(tempFile.toString());

        assertEquals(uri, resource.uri());
        assertEquals(content, resource.readAsString());
    }

    @Test
    public void resources() throws Exception {
        final Path tempDirectory = Files.createTempDirectory(null);
        final Path fileA = Path.of("a.txt");
        final Path fileB = Path.of("foo/b.txt");
        Files.createDirectories(tempDirectory.resolve(fileB.getParent()));
        Files.createFile(tempDirectory.resolve(fileA));
        Files.createFile(tempDirectory.resolve(fileB));

        final String pattern = tempDirectory + File.separator + "**.txt";

        // check if correctly resolves non-existing paths
        assertTrue(factory.resources("/not/existing", _ -> true).isEmpty());

        // check if correctly resolves pattern path
        final List<Resource> resources1 = factory.resources(tempDirectory + "/**", _ -> true);
        assertEquals(2, resources1.size());
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check if correctly resolves single file path
        final List<Resource> resources2 = factory.resources(tempDirectory.resolve(fileA).toString(), _ -> true);
        assertEquals(1, resources2.size());
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));

        // check if correctly resolves GLOB pattern
        final List<Resource> resources3 = factory.resources(pattern, _ -> true);
        assertEquals(2, resources3.size());
        assertTrue(resources3.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources3.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));
    }
}
