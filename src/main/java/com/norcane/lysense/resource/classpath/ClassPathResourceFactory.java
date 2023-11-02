package com.norcane.lysense.resource.classpath;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceFactory;

import java.net.URI;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClassPathResourceFactory implements ResourceFactory {

    @Override
    public Resource.Scheme scheme() {
        return ClassPathResource.SCHEME;
    }

    @Override
    public Optional<Resource> resource(URI uri) {
        return getClass().getResource(uri.getSchemeSpecificPart()) != null
               ? Optional.of(ClassPathResource.of(uri))
               : Optional.empty();
    }
}
