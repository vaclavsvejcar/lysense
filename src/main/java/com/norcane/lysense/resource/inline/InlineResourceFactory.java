package com.norcane.lysense.resource.inline;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceFactory;

import java.net.URI;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InlineResourceFactory implements ResourceFactory {

    @Override
    public Resource.Scheme scheme() {
        return InlineResource.SCHEME;
    }

    @Override
    public Optional<Resource> resource(URI uri) {
        return Optional.of(InlineResource.of(uri));
    }
}