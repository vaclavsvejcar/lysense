package com.norcane.lysense.resource.classpath;

import com.norcane.lysense.resource.Resource;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
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

        final Optional<Resource> resource = factory.resource(uri);
        assertIsPresent(uri, resource.map(Resource::uri));

        assertTrue(factory.resource(URI.create("classpath://foo")).isEmpty());
    }
}