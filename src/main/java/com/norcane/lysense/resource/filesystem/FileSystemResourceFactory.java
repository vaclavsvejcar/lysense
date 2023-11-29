package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.DefaultResourceFactory;
import com.norcane.lysense.resource.loader.IterableResourceFactory;
import com.norcane.lysense.resource.util.PathMatcher;
import com.norcane.lysense.resource.util.ResourceWalker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
