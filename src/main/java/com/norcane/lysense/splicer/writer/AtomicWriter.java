package com.norcane.lysense.splicer.writer;

import com.norcane.lysense.resource.util.LineSeparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * An implementation of {@link Writer} that writes to a temporary buffer and then atomically transfers the contents to the target writer when this writer is
 * closed.
 */
public abstract class AtomicWriter extends Writer {

    private final Supplier<Writer> targetWriter;
    private final LineSeparator lineSeparator;
    private Writer tempWriter;

    /**
     * Constructs a new {@link AtomicWriter} with the given target writer and line separator.
     *
     * @param targetWriter  the target writer
     * @param lineSeparator the line separator
     */
    public AtomicWriter(Supplier<Writer> targetWriter, LineSeparator lineSeparator) {
        this.targetWriter = nonNull(targetWriter);
        this.lineSeparator = nonNull(lineSeparator);
    }

    /**
     * Returns {@link Writer} that should be used to write to the temporary buffer.
     *
     * @return the temporary buffer writer
     */
    protected abstract Writer tempWriter();

    /**
     * Returns {@link Reader} that should be used to read from the temporary buffer.
     *
     * @return the temporary buffer reader
     */
    protected abstract Reader tempReader();

    /**
     * Appends the given line to the writer.
     *
     * @param line the line to append
     * @throws IOException if an I/O error occurs
     */
    public void appendLine(String line) throws IOException {
        ensureTempWriter();
        tempWriter.append(line).append(lineSeparator.separator());
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        ensureTempWriter();
        tempWriter.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        ensureTempWriter();
        tempWriter.flush();
    }

    @Override
    public void close() throws IOException {
        try (final BufferedReader reader = new BufferedReader(tempReader());
             final BufferedWriter writer = new BufferedWriter(targetWriter.get())) {

            reader.transferTo(writer);
        }

        ensureTempWriter();
        tempWriter.close();
    }

    private void ensureTempWriter() {
        tempWriter = (tempWriter == null ? tempWriter() : tempWriter);
    }
}
