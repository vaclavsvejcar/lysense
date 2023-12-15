package com.norcane.lysense.splicer.writer;

import com.norcane.lysense.resource.util.LineSeparator;

import java.io.Writer;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * A factory for creating {@link AtomicWriter} instances.
 */
@ApplicationScoped
public class AtomicWriterFactory {

    /**
     * Constructs a new {@link AtomicWriter} with the given target writer and line separator.
     *
     * @param targetWriter  the target writer
     * @param lineSeparator the line separator
     * @return the new {@link AtomicWriter} instance
     */
    public AtomicWriter of(Supplier<Writer> targetWriter, LineSeparator lineSeparator) {
        return new InMemoryAtomicWriter(targetWriter, lineSeparator);
    }
}
