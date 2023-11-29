package com.norcane.lysense.resource.util;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.filesystem.FileSystemResource;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ResourceWalkerTest {

    @Inject
    ResourceWalker resourceWalker;

    @Test
    public void walk() throws Exception {
        final Path tempDirectory = Files.createTempDirectory(null);
        final Path fileA = Path.of("a.txt");
        final Path fileB = Path.of("foo/b.txt");
        Files.createDirectories(tempDirectory.resolve(fileB.getParent()));
        Files.createFile(tempDirectory.resolve(fileA));
        Files.createFile(tempDirectory.resolve(fileB));

        final String root = tempDirectory.toString();

        final List<Resource> resources = resourceWalker.walk(Path.of(root), "**.txt", FileSystemResource::of, _ -> true);
        assertEquals(2, resources.size());
        assertTrue(resources.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));
    }
}