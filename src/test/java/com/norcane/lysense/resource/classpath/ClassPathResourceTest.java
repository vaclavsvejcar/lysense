package com.norcane.lysense.resource.classpath;

import com.google.common.io.CharStreams;

import com.norcane.lysense.resource.LineSeparator;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.toolkit.net.URIs;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class ClassPathResourceTest {

    static final String content = "Hello\nthere!";
    static final URI uri = URIs.create("classpath:/classpath-resource.txt");
    static final Resource resource = ClassPathResource.of("/classpath-resource.txt");

    @Test
    void of() {
        final ClassPathResource classPathResource1 = ClassPathResource.of(URI.create("classpath:/classpath-resource.txt"));
        final ClassPathResource classPathResource2 = ClassPathResource.of("/classpath-resource.txt");
        assertEquals(classPathResource1, classPathResource2);
    }

    @Test
    void name() {
        assertEquals("classpath-resource", resource.name());
    }

    @Test
    void extension() {
        assertEquals("txt", resource.extension());
    }

    @Test
    void uri() {
        assertEquals(uri, resource.uri());
    }

    @Test
    void reader() throws Exception {
        try (final Reader reader = resource.reader()) {
            assertEquals(content, CharStreams.toString(reader));
        }
    }

    @Test
    void readAsString() {
        assertEquals(content, resource.readAsString());
        assertThrows(ResourceNotFoundException.class, () -> ClassPathResource.of(URIs.create("classpath:/not/existing")).readAsString());
    }

    @Test
    void lineSeparator() {
        assertEquals(LineSeparator.LF, resource.lineSeparator());
    }

}