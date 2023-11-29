package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.DefaultResourceFactory;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FileSystemResourceFactoryTest {

    @Inject
    @DefaultResourceFactory
    FileSystemResourceFactory factory;

    @Test
    void scheme() {
        assertEquals(FileSystemResource.SCHEME, factory.scheme());
    }

    @Test
    public void resource() throws Exception {
        final String content = "Hello, there!";
        final Path tempFile = Files.createTempFile(null, null);
        Files.writeString(tempFile, content);

        final URI uri = tempFile.toUri();
        final Resource resource = factory.resource(tempFile.toString());

        assertEquals(uri, resource.uri());
        assertEquals(content, resource.readAsString());
    }

    @Test
    public void resources() throws Exception {
        final Path tempDirectory = Files.createTempDirectory(null);
        final Path fileA = Path.of("a.txt");
        final Path fileB = Path.of("foo/b.txt");
        Files.createDirectories(tempDirectory.resolve(fileB.getParent()));
        Files.createFile(tempDirectory.resolve(fileA));
        Files.createFile(tempDirectory.resolve(fileB));

        final String pattern = tempDirectory + File.separator + "**.txt";

        // check if correctly resolves non-existing paths
        assertTrue(factory.resources("/not/existing", _ -> true).isEmpty());

        // check if correctly resolves pattern path
        final List<Resource> resources1 = factory.resources(tempDirectory + "/**", _ -> true);
        assertEquals(2, resources1.size());
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources1.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check if correctly resolves single file path
        final List<Resource> resources2 = factory.resources(tempDirectory.resolve(fileA).toString(), _ -> true);
        assertEquals(1, resources2.size());
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));

        // check if correctly resolves GLOB pattern
        final List<Resource> resources3 = factory.resources(pattern, _ -> true);
        assertEquals(2, resources3.size());
        assertTrue(resources3.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources3.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));
    }
}