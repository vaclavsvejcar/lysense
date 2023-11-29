package com.norcane.lysense.resource.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

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
     * @param path path to match
     * @return {@code true} if {@code path} matches {@code pattern}
     */
    public boolean matches(String pattern, Path path) {
        final String syntaxAndPattern = PATH_MATCHER_SYNTAX + ":" + pattern;
        return FileSystems.getDefault().getPathMatcher(syntaxAndPattern).matches(path);
    }

    /**
     * Matches given {@code path} against <i>GLOB</i> {@code pattern}.
     *
     * @param pattern pattern to match against
     * @param path path to match
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
