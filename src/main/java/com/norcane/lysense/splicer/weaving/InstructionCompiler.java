package com.norcane.lysense.splicer.weaving;

import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.splicer.Operation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Compiler that produces optimized {@link ExecutionPlan} based on given {@link Operation} that allows to execute splicing using {@link WeavingResourceSplicer}
 * in the most efficient way.
 *
 * @see ExecutionPlan
 * @see Instruction
 * @see WeavingResourceSplicer
 */
@ApplicationScoped
public class InstructionCompiler {

    /**
     * Compiles given {@link Operation} into optimized {@link ExecutionPlan} of {@link Instruction}s that can be executed by the
     * {@link WeavingResourceSplicer}.
     *
     * @param operation operation to be compiled
     */
    public ExecutionPlan compile(Operation operation) {
        return switch (operation) {
            case Operation.AddSection(var startLine, var endLine) -> compileAddSection(startLine, endLine);
            case Operation.DropSection(var startLine, var endLine) -> compileDropSection(startLine, endLine);
            case Operation.ReplaceSection(var startLine, var endLine, var section) -> compileReplaceSection(startLine, endLine, section);
        };
    }

    private ExecutionPlan compileAddSection(int startLine, String section) {
        final Stream<? extends Instruction> instructions = InlineResource.of(section).readLines().stream()
            .map(Instruction::insertLine);

        return new ExecutionPlan(
            startLine,
            Instruction.composite(Stream.concat(instructions, Stream.of(Instruction.writeSourceLine())).toList()));
    }

    private ExecutionPlan compileDropSection(int startLine, int endLine) {
        final Instruction instruction = startLine == endLine
                                        ? Instruction.skipSourceLine()
                                        : Instruction.composite(Collections.nCopies((endLine - startLine) + 1, Instruction.skipSourceLine()));

        return new ExecutionPlan(startLine, instruction);
    }

    private ExecutionPlan compileReplaceSection(int startLine, int endLine, String section) {
        final List<String> lines = InlineResource.of(section).readLines();

        final List<Instruction> skipInstructions = Collections.nCopies((endLine - startLine) + 1, Instruction.skipSourceLine());
        final List<Instruction.InsertLine> insertInstructions = lines.stream().map(Instruction::insertLine).toList();
        final Instruction instruction = Instruction.composite(Stream.concat(skipInstructions.stream(), insertInstructions.stream()).toList());

        return new ExecutionPlan(startLine, instruction);
    }
}
