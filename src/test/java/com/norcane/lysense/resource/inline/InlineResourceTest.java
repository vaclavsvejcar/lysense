package com.norcane.lysense.resource.inline;

import org.junit.jupiter.api.Test;

import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class InlineResourceTest {

    @Test
    void of_uri() {
        final URI uri = URI.create("inline:java;name=hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh");
        final InlineResource resource = InlineResource.of(uri);

        assertEquals("java", resource.extension());
        assertEquals("hello world", resource.name());
        assertEquals("The Cake is a Lie!", resource.readAsString());
    }

    @Test
    void of_params() {
        final URI uri = URI.create("inline:java;name=hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh");

        final InlineResource resource = InlineResource.of("hello world", "java", "The Cake is a Lie!");

        assertEquals("java", resource.extension());
        assertEquals("hello world", resource.name());
        assertEquals("The Cake is a Lie!", resource.readAsString());
        assertEquals(uri, resource.location());
    }
}