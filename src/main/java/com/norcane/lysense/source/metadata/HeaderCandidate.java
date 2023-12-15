package com.norcane.lysense.source.metadata;

/**
 * Represents the candidate location in source code where new license header can be generated. Note that lines are numbered from {@code 1}.
 *
 * @param putAfterLine    after which line to put new license header (or {@code 0} if it should be put right at the start of the file)
 * @param blankLinesAfter blank lines after the {@link HeaderCandidate#putAfterLine()}
 */
public record HeaderCandidate(int putAfterLine, int blankLinesAfter) {
}
