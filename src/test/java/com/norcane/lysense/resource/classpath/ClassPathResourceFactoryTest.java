package com.norcane.lysense.resource.classpath;

import com.norcane.lysense.resource.Resource;

import org.junit.jupiter.api.Test;

import java.net.URI;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}