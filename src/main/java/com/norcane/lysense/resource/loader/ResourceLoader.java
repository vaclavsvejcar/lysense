package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;

import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

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
        final Optional<Resource.Scheme> maybeScheme = Resource.Scheme.parse(path);
        final ResourceFactory factory = maybeScheme
            .flatMap(scheme -> Optional.ofNullable(factories.get(scheme)))
            .orElse(defaultFactory);

        return factory.resource(maybeScheme.map(scheme -> dropScheme(scheme, path)).orElse(path));
    }

    private String dropScheme(Resource.Scheme scheme, String string) {
        final String prefix = scheme.value() + ":";
        return string.startsWith(prefix) ? string.substring(prefix.length()) : string;
    }
}
