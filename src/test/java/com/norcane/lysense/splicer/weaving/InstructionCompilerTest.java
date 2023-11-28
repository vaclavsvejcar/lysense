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