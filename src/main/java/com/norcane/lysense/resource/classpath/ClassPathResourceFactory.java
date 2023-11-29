package com.norcane.lysense.resource.classpath;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.filesystem.FileSystemResource;
import com.norcane.lysense.resource.loader.IterableResourceFactory;
import com.norcane.lysense.resource.util.PathMatcher;
import com.norcane.lysense.resource.util.ResourceWalker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static com.norcane.toolkit.Prelude.streamOf;

/**
 * Support for accessing resources from <i>classpath</i>, including support for scanning for resources using <i>GLOB</i> patterns. *
 * <br><br>
 * Use {@code classpath} <i>source ID</i> to access resource from this factory via {@link com.norcane.lysense.resource.loader.ResourceLoader}.
 *
 * @see Resource
 * @see IterableResourceFactory
 * @see PathMatcher
 * @see ResourceWalker
 */
@ApplicationScoped
public class ClassPathResourceFactory implements IterableResourceFactory {

    private static final String URI_SCHEME_JAR = "jar";

    private final PathMatcher pathMatcher;
    private final ResourceWalker resourceWalker;

    @Inject
    public ClassPathResourceFactory(PathMatcher pathMatcher,
                                    ResourceWalker resourceWalker) {

        this.pathMatcher = pathMatcher;
        this.resourceWalker = resourceWalker;
    }

    @Override
    public Resource.Scheme scheme() {
        return ClassPathResource.SCHEME;
    }

    @Override
    public Resource resource(String path) {
        return ClassPathResource.of(path);
    }

    @Override
    public List<Resource> resources(String locationGlobPattern, Predicate<Resource> filter) {
        final String rootPathString = pathMatcher.resolveRootPath(locationGlobPattern);
        final String pattern = locationGlobPattern.substring(rootPathString.length());

        return classPathResources(rootPathString)
            .flatMap(url -> findResources(url, rootPathString, pattern, filter).stream())
            .toList();
    }

    private List<Resource> findResources(URL url, String rootPathString, String pattern, Predicate<Resource> filter) {
        return URI_SCHEME_JAR.equals(url.getProtocol())
               ? findJarResources(url, rootPathString, pattern, filter)
               : findFileResources(url, pattern, filter);
    }

    private List<Resource> findJarResources(URL url, String rootPathString, String pattern, Predicate<Resource> filter) {
        try (final FileSystem fs = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap())) {
            final Path rootPath = fs.getPath(rootPathString);

            return resourceWalker.walk(rootPath, pattern, this::toResource, filter);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Resource> findFileResources(URL url, String pattern, Predicate<Resource> filter) {
        try {
            final Path rootPath = Path.of(url.toURI());

            // if root path doesn't exist, don't try to iterate it at all
            if (!Files.exists(rootPath)) {
                return Collections.emptyList();
            }

            return resourceWalker.walk(rootPath, pattern, FileSystemResource::of, filter);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<URL> classPathResources(String name) {
        try {
            return streamOf(getClass().getClassLoader().getResources(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Resource toResource(Path path) {
        return ClassPathResource.of(path.toString());
    }
}
