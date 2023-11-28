package com.norcane.lysense.splicer.weaving;

import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.splicer.Operation;
import com.norcane.lysense.test.InMemoryWritableResourceWrapper;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class WeavingResourceSplicerTest {


    @Inject
    WeavingResourceSplicer splicer;

    @Test
    void splice_addSection_firstLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.addSection(1, "INS"));

        assertEquals("INS\none\ntwo\nthree\n", resource.writtenString());
    }

    @Test
    void splice_addSection_singleLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.addSection(2, "INS"));

        assertEquals("one\nINS\ntwo\nthree\n", resource.writtenString());
    }

    @Test
    void splice_addSection_moreLines() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.addSection(2, "FOO\nBAR"));

        assertEquals("one\nFOO\nBAR\ntwo\nthree\n", resource.writtenString());
    }

    @Test
    void splice_dropSection_singleLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.dropSection(2, 2));

        assertEquals("one\nthree\n", resource.writtenString());
    }

    @Test
    void splice_dropSection_multiLines() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\nfour\n"));
        splicer.splice(resource, Operation.dropSection(2, 3));

        assertEquals("one\nfour\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_singleLine() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.replaceSection(2, 2, "2"));

        assertEquals("one\n2\nthree\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_singleToMulti() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\n"));
        splicer.splice(resource, Operation.replaceSection(2, 2, "2.1\n2.2"));

        assertEquals("one\n2.1\n2.2\nthree\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_multiToMulti() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\nfour\n"));
        splicer.splice(resource, Operation.replaceSection(2, 3, "2\n3"));

        assertEquals("one\n2\n3\nfour\n", resource.writtenString());
    }

    @Test
    void splice_replaceSection_multiToSingle() {
        final var resource = new InMemoryWritableResourceWrapper(InlineResource.of("one\ntwo\nthree\nfour\n"));
        splicer.splice(resource, Operation.replaceSection(2, 3, "2and3"));

        assertEquals("one\n2and3\nfour\n", resource.writtenString());
    }
}