package com.norcane.lysense.splicer.weaving;

/**
 * Represents a single execution plan for the {@link WeavingResourceSplicer} that describes how to splice new content into the resource, using the optimized
 * instruction set.
 *
 * @param line        line where the splicing should happen
 * @param instruction instruction that should be executed
 * @see Instruction
 * @see InstructionCompiler
 * @see WeavingResourceSplicer
 */
public record ExecutionPlan(int line, Instruction instruction) {
}
