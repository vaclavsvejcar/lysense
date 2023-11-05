package com.norcane.lysense.resource.classpath;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClassPathResourceFactory implements ResourceFactory {

    @Override
    public Resource.Scheme scheme() {
        return ClassPathResource.SCHEME;
    }

    @Override
    public Resource resource(String path) {
        return ClassPathResource.of(path);
    }
}