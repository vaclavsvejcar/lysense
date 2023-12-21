/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.norcane.lysense.splicer.writer;

import com.norcane.lysense.resource.util.LineSeparator;
import jakarta.annotation.Nonnull;

import java.io.*;
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
    public void write(@Nonnull char[] cbuf, int off, int len) throws IOException {
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
