package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.DefaultResourceFactory;
import com.norcane.lysense.resource.loader.ResourceFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@DefaultResourceFactory
public class FileSystemResourceFactory implements ResourceFactory {

    @Override
    public Resource.Scheme scheme() {
        return FileSystemResource.SCHEME;
    }

    @Override
    public Resource resource(String path) {
        return FileSystemResource.of(path);
    }
}
