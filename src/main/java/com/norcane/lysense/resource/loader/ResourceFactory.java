package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;

public interface ResourceFactory {

    Resource.Scheme scheme();

    Resource resource(String path);
}
