package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.lysense.resource.util.PathMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static com.norcane.toolkit.Prelude.nonNull;
import static com.norcane.toolkit.Prelude.toMap;

@ApplicationScoped
public class ResourceLoader {

    private static final String GLOB_SINGLE_DIR = "*";
    private static final String GLOB_RECURSIVE_DIR = "**";

    private final Map<Resource.Scheme, ResourceFactory> factories;
    private final PathMatcher pathMatcher;
    private final ResourceFactory defaultFactory;

    @Inject
    public ResourceLoader(Instance<ResourceFactory> factories,
                          PathMatcher pathMatcher,
                          @DefaultResourceFactory ResourceFactory defaultFactory) {

        this.factories = toMap(ResourceFactory::scheme, factories);
        this.pathMatcher = pathMatcher;
        this.defaultFactory = defaultFactory;
    }

    /**
     * Loads single resource specified by its <i>path</i>. Path consists of <i>source ID</i> and <i>path</i> separated by colon (e.g.
     * {@code classpath:/foo/bar.txt}). If no <i>source ID</i> prefix is used, then {@link ResourceFactory} annotated with {@link DefaultResourceFactory} is
     * used. No <i>GLOB</i> wildcards or directory paths are allowed, for more flexible resource loading use {@link #resources(String, Predicate, boolean)}.
     *
     * @param path location of the resource
     * @return loaded resource
     * @throws ResourceNotFoundException if the resource cannot be found or location points to directory
     */
    public Resource resource(String path) {
        nonNull(path);

        return findFactory(path).resource(dropScheme(path));
    }

    /**
     * Loads all resources matching the given <i>pattern</i>. Pattern can be either path to concrete file, directory or <i>GLOB</i> pattern. If the location is
     * directory, then based on {@code listDirectoryRecursively} parameter, either all resources in the directory and subdirectories are recursively returned or
     * only the ones in the top-level directory.  Location consists of <i>source ID</i> and <i>path</i> separated by colon (e.g.
     * {@code classpath:/foo/bar.txt}). If no <i>source ID</i> prefix is used, then {@link ResourceFactory} annotated with {@link DefaultResourceFactory} is
     * used. If no resource is found, then empty list is returned.
     *
     * <br><br><b>Given the following directory structure:</b>
     * <pre>
     *   resources/
     *   ├─sub-resources/
     *   │ └─b.txt
     *   └─a.txt
     * </pre>
     *
     * <b>Following resources are returned:</b>
     * {@snippet lang = "java":
     *   resources("file:resources", _ -> true, false);        // [resources/a.txt]
     *   resources("file:resources", _ -> true, true);         // [resources/a.txt, resources/sub-resources/b.txt]
     *   resources("file:resources/a.txt", _ -> true, false);  // [resources/a.txt]
     *   resources("file:**.txt", _ -> true, false);           // [resources/a.txt, resources/sub-resources/b.txt]
     *}
     *
     * @param pattern                  file, directory or <i>GLOB</i> pattern
     * @param filter                   filter do decide whether to include the resource in the result or not
     * @param listDirectoryRecursively if the given location is directory, then whether to list all resources in the directory recursively or only the ones in
     *                                 the top-level directory
     * @return list of loaded resources
     */
    public List<Resource> resources(String pattern, Predicate<Resource> filter, boolean listDirectoryRecursively) {
        final ResourceFactory factory = findFactory(pattern);

        // fail fast if the factory doesn't support iterating resources
        if (!(factory instanceof IterableResourceFactory iterableFactory)) {
            throw new UnsupportedOperationException(STR."Iterating over resources with scheme '\{factory.scheme()}' is not supported");
        }

        // strip the scheme from the pattern (e.g. 'classpath:' or 'file:')
        final String patternWithoutScheme = dropScheme(pattern);

        final Resource resource = resourceOrNull(factory, patternWithoutScheme);
        if (resource != null) {
            return List.of(resource);
        }

        // if the pattern points to directory, return recursively all resources in it
        final String dirSuffix = listDirectoryRecursively ? GLOB_RECURSIVE_DIR : GLOB_SINGLE_DIR;
        final String patternOrDirectory = pathMatcher.isPattern(patternWithoutScheme)
                                          ? patternWithoutScheme
                                          : patternWithoutScheme + "/" + dirSuffix;

        return iterableFactory.resources(patternOrDirectory, filter);
    }

    private ResourceFactory findFactory(String path) {
        final Optional<Resource.Scheme> maybeScheme = Resource.Scheme.parse(path);
        return maybeScheme
            .flatMap(scheme -> Optional.ofNullable(factories.get(scheme)))
            .orElse(defaultFactory);
    }

    private String dropScheme(String path) {
        return Resource.Scheme.parse(path)
            .filter(scheme -> path.startsWith(scheme.value() + ":"))
            .map(scheme -> path.substring(scheme.value().length() + 1))
            .orElse(path);
    }

    private Resource resourceOrNull(ResourceFactory factory, String path) {
        try {
            return factory.resource(path);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
}
