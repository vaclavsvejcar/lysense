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

import com.norcane.lysense.splicer.Operation;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class InstructionCompilerTest {

    @Inject
    InstructionCompiler compiler;

    @Test
    void compile_addSection_singleLine() {
        final var expected = new ExecutionPlan(12, Instruction.composite(
            Instruction.insertLine("hello"), Instruction.writeSourceLine()));
        final var actual = compiler.compile(Operation.addSection(12, "hello"));

        assertEquals(expected, actual);
    }

    @Test
    void compile_addSection_moreLines() {
        final var expected = new ExecutionPlan(12, Instruction.composite(
            Instruction.insertLine("hello"),
            Instruction.insertLine("world"),
            Instruction.writeSourceLine()));
        final var actual = compiler.compile(Operation.addSection(12, "hello\nworld"));

        assertEquals(expected, actual);
    }

    @Test
    void compile_dropSection_singleLine() {
        final var expected = new ExecutionPlan(12, Instruction.skipSourceLine());
        final var actual = compiler.compile(Operation.dropSection(12, 12));

        assertEquals(expected, actual);
    }

    @Test
    void compile_dropSection_moreLines() {
        final var expected = new ExecutionPlan(12, Instruction.composite(Instruction.skipSourceLine(), Instruction.skipSourceLine()));
        final var actual = compiler.compile(Operation.dropSection(12, 13));

        assertEquals(expected, actual);
    }

    @Test
    void compile_replaceSection_singleToSingle() {
        final var expected = new ExecutionPlan(12, Instruction.composite(
            Instruction.skipSourceLine(),
            Instruction.insertLine("hello")));
        final var actual = compiler.compile(Operation.replaceSection(12, 12, "hello"));

        assertEquals(expected, actual);
    }

    @Test
    void compile_replaceSection_singleToMulti() {
        final var expected = new ExecutionPlan(12, Instruction.composite(
            Instruction.skipSourceLine(),
            Instruction.insertLine("hello"),
            Instruction.insertLine("world")));
        final var actual = compiler.compile(Operation.replaceSection(12, 12, "hello\nworld"));

        assertEquals(expected, actual);
    }

    @Test
    void compile_replaceSection_multiToMulti() {
        final var expected = new ExecutionPlan(12, Instruction.composite(
            Instruction.skipSourceLine(),
            Instruction.skipSourceLine(),
            Instruction.insertLine("hello"),
            Instruction.insertLine("world")));
        final var actual = compiler.compile(Operation.replaceSection(12, 13, "hello\nworld"));

        assertEquals(expected, actual);
    }

    @Test
    void compile_replaceSection_multiToSingle() {
        final var expected = new ExecutionPlan(12, Instruction.composite(
            Instruction.skipSourceLine(),
            Instruction.skipSourceLine(),
            Instruction.insertLine("hello")));
        final var actual = compiler.compile(Operation.replaceSection(12, 13, "hello"));

        assertEquals(expected, actual);
    }
}
