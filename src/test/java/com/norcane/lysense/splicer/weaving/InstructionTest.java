package com.norcane.lysense.splicer.weaving;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class InstructionTest {

    @Test
    void toString_SkipSourceLine() {
        assertEquals("SkipSourceLine", Instruction.skipSourceLine().toString());
    }

    @Test
    void toString_WriteSourceLine() {
        assertEquals("WriteSourceLine", Instruction.writeSourceLine().toString());
    }
}