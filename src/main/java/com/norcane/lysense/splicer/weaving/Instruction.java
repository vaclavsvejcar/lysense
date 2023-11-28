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
