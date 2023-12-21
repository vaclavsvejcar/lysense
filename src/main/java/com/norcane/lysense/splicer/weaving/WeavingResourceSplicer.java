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
package com.norcane.lysense.splicer.weaving;

import com.norcane.lysense.resource.WritableResource;
import com.norcane.lysense.resource.exception.CannotWriteResourceException;
import com.norcane.lysense.splicer.Operation;
import com.norcane.lysense.splicer.ResourceSplicer;
import com.norcane.lysense.splicer.writer.AtomicWriter;
import com.norcane.lysense.splicer.writer.AtomicWriterFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Implementation of {@link ResourceSplicer} that weaves the changes defined by the {@link Operation} into the resource in a single pass. To achieve this, it
 * compiles the <i>splicing operation</i> into the optimized {@link ExecutionPlan}.
 */
@ApplicationScoped
public class WeavingResourceSplicer implements ResourceSplicer {

    private final InstructionCompiler compiler;
    private final AtomicWriterFactory writerFactory;

    @Inject
    public WeavingResourceSplicer(InstructionCompiler compiler, AtomicWriterFactory writerFactory) {
        this.compiler = compiler;
        this.writerFactory = writerFactory;
    }

    @Override
    public void splice(WritableResource resource, Operation operation) {
        final ExecutionPlan executionPlan = compiler.compile(operation);

        try (final BufferedReader reader = new BufferedReader(resource.reader());
             final AtomicWriter writer = writerFactory.of(resource::writer, resource.lineSeparator())) {

            boolean shouldContinue = true;
            for (int currentLine = 0; shouldContinue; currentLine++) {

                shouldContinue = currentLine == executionPlan.line()
                        ? interpretInstruction(executionPlan.instruction(), reader, writer)
                        : currentLine <= 0 || copyLine(reader, writer);
            }

        } catch (IOException e) {
            throw new CannotWriteResourceException(resource, e);
        }
    }

    private boolean interpretInstruction(Instruction instruction, BufferedReader reader, AtomicWriter writer) throws IOException {
        switch (instruction) {
            case Instruction.InsertLine insertLine -> {
                writer.appendLine(insertLine.line());
                return true;
            }
            case Instruction.SkipSourceLine _ -> {
                return reader.readLine() != null;
            }
            case Instruction.WriteSourceLine _ -> {
                return copyLine(reader, writer);
            }
            case Instruction.Composite composite -> {
                boolean shouldContinue = true;
                for (final Instruction subInstruction : composite.instructions()) {
                    final boolean result = interpretInstruction(subInstruction, reader, writer);
                    shouldContinue = (!shouldContinue || result) && shouldContinue;
                }
                return shouldContinue;
            }
        }
    }

    private boolean copyLine(BufferedReader reader, AtomicWriter writer) throws IOException {
        final String line = reader.readLine();

        if (line == null) {
            return false;
        }

        writer.appendLine(line);
        return true;
    }
}
