package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;

import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static com.norcane.toolkit.Prelude.nonNull;
import static com.norcane.toolkit.Prelude.toMap;

@ApplicationScoped
public class ResourceLoader {

    private final Map<Resource.Scheme, ResourceFactory> factories;
    private final ResourceFactory defaultFactory;

    @Inject
    public ResourceLoader(Instance<ResourceFactory> factories,
                          @DefaultResourceFactory ResourceFactory defaultFactory) {

        this.factories = toMap(ResourceFactory::scheme, factories);
        this.defaultFactory = defaultFactory;
    }

    public Resource resource(String path) {
        nonNull(path);

        return findFactory(path).resource(dropScheme(path));
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
}
