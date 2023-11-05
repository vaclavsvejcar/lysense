package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;

import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class ResourceLoaderTest {

    @Inject
    ResourceLoader resourceLoader;

    @Test
    void resource() {
        // check that concrete resource is loaded
        final Resource resource = resourceLoader.resource("classpath:/classpath-resource.txt");
        assertEquals(URI.create("classpath:/classpath-resource.txt"), resource.uri());

        // check that exception is thrown for non-existent resource
        assertThrows(ResourceNotFoundException.class, () -> resourceLoader.resource("/foo/bar.txt"));

        // check that exception is thrown for non-file resource
        assertThrows(ResourceNotFoundException.class, () -> resourceLoader.resource("/resources-test"));
    }
}