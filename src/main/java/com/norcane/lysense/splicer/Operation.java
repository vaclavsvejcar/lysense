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
     * @param section   section to be added
     */
    record AddSection(int startLine, String section) implements Operation {
    }

    /**
     * Drops section from the resource between given starting and ending line (including). Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine   ending line
     */
    record DropSection(int startLine, int endLine) implements Operation {
    }

    /**
     * Replaces section in the resource between given starting and ending line (including) with new content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine   ending line
     * @param section   section to be added
     */
    record ReplaceSection(int startLine, int endLine, String section) implements Operation {
    }

    /**
     * Adds new section to the resource at given starting line and moves the existing content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param section   section to be added
     */
    static AddSection addSection(int startLine, String section) {
        return new AddSection(startLine, section);
    }

    /**
     * Drops section from the resource between given starting and ending line (including). Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine   ending line
     */
    static DropSection dropSection(int startLine, int endLine) {
        return new DropSection(startLine, endLine);
    }

    /**
     * Replaces section in the resource between given starting and ending line (including) with new content. Note that lines are indexed from 1.
     *
     * @param startLine starting line
     * @param endLine   ending line
     * @param section   section to be added
     */
    static ReplaceSection replaceSection(int startLine, int endLine, String section) {
        return new ReplaceSection(startLine, endLine, section);
    }
}
