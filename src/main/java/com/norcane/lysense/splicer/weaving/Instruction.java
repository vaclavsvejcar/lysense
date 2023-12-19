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

import java.util.Arrays;
import java.util.List;

/**
 * Represents a single <i>instruction</i> produced by the {@link InstructionCompiler} that describes what to do at given time and place during the weaving
 * process.
 *
 * @see InstructionCompiler
 * @see WeavingResourceSplicer
 */
public sealed interface Instruction permits Instruction.Composite,
                                            Instruction.InsertLine,
                                            Instruction.SkipSourceLine,
                                            Instruction.WriteSourceLine {

    /**
     * Composite instruction that contains multiple other instructions that should be executed in order.
     *
     * @param instructions instructions to be executed
     */
    record Composite(List<? extends Instruction> instructions) implements Instruction {
    }

    /**
     * Inserts new line into the target resource.
     *
     * @param line line to be inserted
     */
    record InsertLine(String line) implements Instruction {
    }

    /**
     * Skips the line in the source resource.
     */
    final class SkipSourceLine implements Instruction {
        private static final SkipSourceLine INSTANCE = new SkipSourceLine();

        private SkipSourceLine() {
        }

        private static SkipSourceLine instance() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /**
     * Writes the line from the source resource into the target resource.
     */
    final class WriteSourceLine implements Instruction {
        private static final WriteSourceLine INSTANCE = new WriteSourceLine();

        private WriteSourceLine() {
        }

        private static WriteSourceLine instance() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /**
     * Composite instruction that contains multiple other instructions that should be executed in order.
     *
     * @param instructions instructions to be executed
     */
    static Composite composite(List<Instruction> instructions) {
        return new Composite(instructions);
    }

    /**
     * Composite instruction that contains multiple other instructions that should be executed in order.
     *
     * @param instructions instructions to be executed
     */
    static Composite composite(Instruction... instructions) {
        return new Composite(Arrays.stream(instructions).toList());
    }

    /**
     * Inserts new line into the target resource.
     *
     * @param line line to be inserted
     */
    static InsertLine insertLine(String line) {
        return new InsertLine(line);
    }

    /**
     * Skips the line in the source resource.
     */
    static SkipSourceLine skipSourceLine() {
        return SkipSourceLine.instance();
    }

    /**
     * Writes the line from the source resource into the target resource.
     */
    static WriteSourceLine writeSourceLine() {
        return WriteSourceLine.instance();
    }
}
