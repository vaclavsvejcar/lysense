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
import com.norcane.lysense.resource.loader.IterableResourceFactory;
import com.norcane.lysense.resource.util.PathMatcher;
import com.norcane.lysense.resource.util.ResourceWalker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ApplicationScoped
@DefaultResourceFactory
public class FileSystemResourceFactory implements IterableResourceFactory {

    private final PathMatcher pathMatcher;
    private final ResourceWalker resourceWalker;

    @Inject
    public FileSystemResourceFactory(PathMatcher pathMatcher,
                                     ResourceWalker resourceWalker) {

        this.pathMatcher = pathMatcher;
        this.resourceWalker = resourceWalker;
    }

    @Override
    public Resource.Scheme scheme() {
        return FileSystemResource.SCHEME;
    }

    @Override
    public Resource resource(String path) {
        return FileSystemResource.of(path);
    }

    @Override
    public List<Resource> resources(String locationGlobPattern, Predicate<Resource> filter) {
        final String rootPathString = pathMatcher.resolveRootPath(locationGlobPattern);
        final String pattern = locationGlobPattern.substring(rootPathString.length());

        final Path rootPath = Path.of(rootPathString);
        if (!Files.exists(rootPath)) {
            return Collections.emptyList();
        }

        return resourceWalker.walk(rootPath, pattern, FileSystemResource::of, filter);
    }
}
