package com.norcane.lysense.resource.inline;

import com.norcane.lysense.resource.LineSeparator;
import com.norcane.toolkit.net.URIs;

import org.junit.jupiter.api.Test;

import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class InlineResourceTest {

    @Test
    void of_uri() {
        final URI uri = URI.create("inline:java;name=hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh");
        final InlineResource resource = InlineResource.of(uri);

        assertEquals("java", resource.extension());
        assertEquals("hello world", resource.name());
        assertEquals("The Cake is a Lie!", resource.readAsString());
        assertEquals(uri, resource.uri());
        assertEquals(LineSeparator.platform(), resource.lineSeparator());
    }

    @Test
    void of_uri_invalidScheme() {
        assertThrows(IllegalArgumentException.class, () -> InlineResource.of(URIs.create("file:///foo/bar.txt")));
    }

    @Test
    void of_params() {
        final URI uri = URIs.create("inline:java;name=hello%20world;base64,VGhlIENha2UgaXMgYSBMaWUh");
        final InlineResource resource = InlineResource.of("hello world", "java", "The Cake is a Lie!");

        assertEquals("java", resource.extension());
        assertEquals("hello world", resource.name());
        assertEquals("The Cake is a Lie!", resource.readAsString());
        assertEquals(uri, resource.uri());
    }
}