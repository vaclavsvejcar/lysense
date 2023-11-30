package com.norcane.lysense.resource.loader;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void resources() throws IOException {
        final Path tempDirectory = Files.createTempDirectory(null);
        final Path fileA = Path.of("a.txt");
        final Path fileB = Path.of("foo/b.txt");
        Files.createDirectories(tempDirectory.resolve(fileB.getParent()));
        Files.createFile(tempDirectory.resolve(fileA));
        Files.createFile(tempDirectory.resolve(fileB));

        // check that all resources from given directory are recursively loaded
        final List<Resource> resources1 = resourceLoader.resources(tempDirectory.toString(), _ -> true, true);
        assertEquals(2, resources1.size());
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check that all resources from given directory are recursively loaded when using '**' GLOB
        final List<Resource> resources2 = resourceLoader.resources(tempDirectory + "/**", _ -> true, true);
        assertEquals(2, resources2.size());
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check that all resources from given directory are non-recursively loaded
        final List<Resource> resources3 = resourceLoader.resources(tempDirectory.toString(), _ -> true, false);
        assertEquals(1, resources3.size());
        assertTrue(resources3.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));

        // check that concrete resource is returned when using absolute path
        final List<Resource> resources4 = resourceLoader.resources(tempDirectory.resolve(fileA).toString(), _ -> true, true);
        assertEquals(1, resources4.size());
        assertTrue(resources4.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
    }
}