package com.norcane.lysense.splicer.writer;

import com.norcane.lysense.resource.util.LineSeparator;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Supplier;

/**
 * An implementation of {@link AtomicWriter} that uses in-memory buffer to store the content.
 *
 * @see AtomicWriter
 */
public class InMemoryAtomicWriter extends AtomicWriter {

    private final StringWriter tempWriter;

    /**
     * Constructs a new {@link InMemoryAtomicWriter} with the given target writer and line separator.
     *
     * @param targetWriter  the target writer
     * @param lineSeparator the line separator
     */
    public InMemoryAtomicWriter(Supplier<Writer> targetWriter, LineSeparator lineSeparator) {
        super(targetWriter, lineSeparator);

        this.tempWriter = new StringWriter();
    }

    @Override
    protected Writer tempWriter() {
        return tempWriter;
    }

    @Override
    protected Reader tempReader() {
        return new StringReader(tempWriter.toString());
    }
}
