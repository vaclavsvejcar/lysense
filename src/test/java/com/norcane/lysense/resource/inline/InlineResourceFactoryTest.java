package com.norcane.lysense.resource.inline;

import com.norcane.lysense.resource.Resource;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class InlineResourceFactoryTest {

    @Inject
    InlineResourceFactory inlineResourceFactory;

    @Test
    void scheme() {
        assertEquals(new Resource.Scheme("inline"), inlineResourceFactory.scheme());
    }

    @Test
    void resource() {
        final Optional<Resource> resource = inlineResourceFactory.resource(URI.create("inline:java;name=hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh"));

        assertTrue(resource.isPresent());
    }
}