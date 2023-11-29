package com.norcane.lysense.resource.filesystem;

import com.google.common.io.CharStreams;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.lysense.resource.util.LineSeparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class FileSystemResourceTest {

    private static final String content = "Hello\nthere!";
    private static Resource resource;
    private static URI uri;

    @BeforeEach
    void init() throws Exception {
        final Path tempFile = Files.createTempFile(null, null);
        Files.writeString(tempFile, content);

        uri = tempFile.toUri();
        resource = FileSystemResource.of(uri);
    }

    @Test
    void of() {
        assertEquals(resource, FileSystemResource.of(uri));
        assertThrows(ResourceNotFoundException.class, () -> FileSystemResource.of("fo"));
    }

    @Test
    void reader() throws Exception {
        try (final Reader reader = resource.reader()) {
            assertEquals(content, CharStreams.toString(reader));
        }
    }

    @Test
    void lineSeparator() {
        assertEquals(LineSeparator.LF, resource.lineSeparator());
    }
}