package com.norcane.lysense.splicer.writer;

import com.norcane.lysense.resource.util.LineSeparator;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class InMemoryAtomicWriterTest {

    @Test
    void tempWriter() throws IOException {
        try (InMemoryAtomicWriter writer = new InMemoryAtomicWriter(StringWriter::new, LineSeparator.LF)) {
            assertNotNull(writer.tempWriter());
        }
    }

    @Test
    void tempReader() throws IOException {
        try (InMemoryAtomicWriter writer = new InMemoryAtomicWriter(StringWriter::new, LineSeparator.LF)) {
            assertNotNull(writer.tempReader());
        }
    }

    @Test
    void write() throws IOException {
        final StringWriter targetWriter = new StringWriter();
        final InMemoryAtomicWriter writer = new InMemoryAtomicWriter(() -> targetWriter, LineSeparator.LF);

        writer.write("foo".toCharArray(), 0, 3);
        writer.flush();
        writer.close();

        assertEquals("foo", targetWriter.toString());
    }
}