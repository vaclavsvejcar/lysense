package com.norcane.lysense.splicer;

/**
 * Represents a <i>splicing operation</i> that can be used to splice a content of given <i>resource</i> with new content.
 *
 * @see ResourceSplicer
 */
public sealed interface Operation permits Operation.AddSection,
                                          Operation.DropSection,
                                          Operation.ReplaceSection {

    /**
     * Adds new section to the resource at given starting line and moves the existing content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param section section to be added
     */
    record AddSection(int startLine, String section) implements Operation {
    }

    /**
     * Drops section from the resource between given starting and ending line (including). Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine ending line
     */
    record DropSection(int startLine, int endLine) implements Operation {
    }

    /**
     * Replaces section in the resource between given starting and ending line (including) with new content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine ending line
     * @param section section to be added
     */
    record ReplaceSection(int startLine, int endLine, String section) implements Operation {
    }

    /**
     * Adds new section to the resource at given starting line and moves the existing content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param section section to be added
     */
    static AddSection addSection(int startLine, String section) {
        return new AddSection(startLine, section);
    }

    /**
     * Drops section from the resource between given starting and ending line (including). Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine ending line
     */
    static DropSection dropSection(int startLine, int endLine) {
        return new DropSection(startLine, endLine);
    }

    /**
     * Replaces section in the resource between given starting and ending line (including) with new content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine ending line
     * @param section section to be added
     */
    static ReplaceSection replaceSection(int startLine, int endLine, String section) {
        return new ReplaceSection(startLine, endLine, section);
    }
}
