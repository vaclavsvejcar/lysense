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

import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class for matching paths against <i>GLOB</i> patterns.
 */
@ApplicationScoped
public class PathMatcher {

    private static final String PATH_MATCHER_SYNTAX = "glob";
    private static final List<Character> SPECIAL_CHARS = List.of('*', '?', '[');

    /**
     * Matches given {@code path} against <i>GLOB</i> {@code pattern}.
     *
     * @param pattern pattern to match against
     * @param path    path to match
     * @return {@code true} if {@code path} matches {@code pattern}
     */
    public boolean matches(String pattern, Path path) {
        final String syntaxAndPattern = STR."\{PATH_MATCHER_SYNTAX}:\{pattern}";
        return FileSystems.getDefault().getPathMatcher(syntaxAndPattern).matches(path);
    }

    /**
     * Matches given {@code path} against <i>GLOB</i> {@code pattern}.
     *
     * @param pattern pattern to match against
     * @param path    path to match
     * @return {@code true} if {@code path} matches {@code pattern}
     */
    public boolean matches(String pattern, String path) {
        return matches(pattern, Path.of(path));
    }

    /**
     * Detects whether the given {@code input} is a <i>GLOB</i> input.
     *
     * @param input input to check
     * @return {@code true} if {@code input} is a <i>GLOB</i> input
     */
    public boolean isPattern(String input) {
        return input.chars().mapToObj(i -> (char) i).anyMatch(SPECIAL_CHARS::contains);
    }

    /**
     * Resolves root path of the given <i>GLOB</i> pattern (i.e. longest possible path without any <i>GLOB</i> characters).
     *
     * <br><br><strong>Example of use</strong>
     * {@snippet lang = "java":
     *      resolveRootPath("/WEB-INF/*.xml");           // returns "/WEB-INF/"
     *      resolveRootPath("/WEB-INF/foo");             // returns "/WEB-INF/foo"
     *      resolveRootPath("/WEB-INF/foo/bar.xml");     // returns "/WEB-INF/foo/bar.xml"
     *}
     *
     * @param location location to resolve root path for
     * @return root path of the given {@code location}
     */
    public String resolveRootPath(String location) {
        int rootDirEnd = location.length();

        while (isPattern(location.substring(0, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }

        return location.substring(0, rootDirEnd);
    }
}
