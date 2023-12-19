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
