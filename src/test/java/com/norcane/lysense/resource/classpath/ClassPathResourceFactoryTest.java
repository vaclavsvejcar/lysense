package com.norcane.lysense.resource.classpath;

import com.norcane.lysense.resource.Resource;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ClassPathResourceFactoryTest {

    @Inject
    ClassPathResourceFactory factory;

    @Test
    void scheme() {
        assertEquals("classpath", factory.scheme().value());
    }

    @Test
    void resource() {
        final URI uri = URI.create("classpath:/classpath-resource.txt");

        final Resource resource = factory.resource("/classpath-resource.txt");
        assertEquals(uri, resource.uri());
    }

    @Test
    void resources() {
        // check if correctly resolves GLOB path
        final List<Resource> resources = factory.resources("/resources-test/**", resource -> true);
        assertEquals(2, resources.size());
        assertTrue(resources.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
        assertTrue(resources.stream().anyMatch(resource -> resource.uri().toString().endsWith("b.txt")));

        // check if correctly resolves single file path
        final List<Resource> resources2 = factory.resources("/resources-test/a.txt", resource -> true);
        assertEquals(1, resources2.size());
        assertTrue(resources2.stream().anyMatch(resource -> resource.uri().toString().endsWith("a.txt")));
    }
}