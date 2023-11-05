package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.DefaultResourceFactory;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}