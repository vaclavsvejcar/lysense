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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Utility class for walking through the NIO file system and finding resources.
 */
@ApplicationScoped
public class ResourceWalker {

    private final PathMatcher pathMatcher;

    @Inject
    public ResourceWalker(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Walks through the given {@code rootPath}, finds all paths matching the given {@code pattern} and converts them to corresponding {@link Resource}.
     *
     * @param rootPath   root path to walk through
     * @param pattern    pattern to match
     * @param toResource function to convert {@link Path} to {@link Resource}
     * @param filter     filter to apply on the resulting resources
     * @return list of resources matching the given {@code pattern}
     * @throws UncheckedIOException if unexpected IO error occurs
     */
    public List<Resource> walk(Path rootPath, String pattern, Function<Path, Resource> toResource, Predicate<Resource> filter) {
        try (final Stream<Path> stream = Files.walk(rootPath)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(path -> pathMatcher.matches(pattern, rootPath.relativize(path)))
                .map(toResource)
                .filter(filter)
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
