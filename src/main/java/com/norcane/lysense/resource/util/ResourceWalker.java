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
