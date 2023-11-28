package com.norcane.lysense.splicer.weaving;

import com.norcane.lysense.resource.WritableResource;
import com.norcane.lysense.resource.exception.CannotWriteResourceException;
import com.norcane.lysense.splicer.Operation;
import com.norcane.lysense.splicer.ResourceSplicer;
import com.norcane.lysense.splicer.writer.AtomicWriter;
import com.norcane.lysense.splicer.writer.AtomicWriterFactory;

import java.io.BufferedReader;
import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
