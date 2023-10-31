package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;

import java.net.URI;
import java.util.Optional;

public interface ResourceFactory {

    Resource.Scheme scheme();

    Optional<Resource> resource(URI uri);
}
