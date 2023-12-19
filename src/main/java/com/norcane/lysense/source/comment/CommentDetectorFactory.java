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
package com.norcane.lysense.source.comment;

import java.util.regex.Pattern;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * <i>Factory class</i> used to produce instances of {@link CommentDetector}. The choice to pass around instance of this factory instead of concrete instances
 * has been made to explicitly prevent sharing instance of {@link CommentDetector} that might be stateful.
 */
public final class CommentDetectorFactory {

    private final Pattern commentStart;
    private final Pattern commentEnd;

    private CommentDetectorFactory(Pattern commentStart, Pattern commentEnd) {
        this.commentStart = commentStart;
        this.commentEnd = commentEnd;
    }

    /**
     * Constructs new instance of {@link CommentDetectorFactory} to detect <i>line syntax</i> comments in source codes.
     *
     * @param lineCommentStart pattern to detect the line comment
     * @return instance of comment detector factory
     */
    public static CommentDetectorFactory lineSyntax(Pattern lineCommentStart) {
        return new CommentDetectorFactory(nonNull(lineCommentStart), null);
    }

    /**
     * Constructs new instance of {@link CommentDetectorFactory} to detect <i>block syntax</i> comments in source codes.
     *
     * @param blockCommentStart pattern to detect start of the comment
     * @param blockCommentEnd   pattern to detect end of the comment
     * @return instance of comment detector factory
     */
    public static CommentDetectorFactory blockSyntax(Pattern blockCommentStart, Pattern blockCommentEnd) {
        return new CommentDetectorFactory(nonNull(blockCommentStart), nonNull(blockCommentEnd));
    }

    /**
     * Creates new instance of {@link CommentDetector}.
     *
     * @return instance of comment detector
     */
    public CommentDetector create() {
        return commentEnd != null ? blockCommentDetector() : lineCommentDetector();
    }

    private CommentDetector blockCommentDetector() {
        return new CommentDetector() {
            private State state = State.LOOKING_FOR_START;

            @Override
            public boolean isComment(String line) {
                return switch (state) {
                    case LOOKING_FOR_START -> checkIfStart(line);
                    case LOOKING_FOR_END -> checkIfEnd(line);
                };
            }

            private boolean checkIfStart(String line) {
                final boolean isStart = commentStart.matcher(line).find();
                final boolean isEnd = commentEnd.matcher(line).find();

                if (isStart && isEnd) {
                    return true;
                } else if (isStart) {
                    state = State.LOOKING_FOR_END;
                    return true;
                }

                return false;
            }

            private boolean checkIfEnd(String line) {
                final boolean isEnd = commentEnd.matcher(line).find();

                if (isEnd) {
                    state = State.LOOKING_FOR_START;
                }

                return true;
            }

            enum State {
                LOOKING_FOR_START, LOOKING_FOR_END
            }
        };
    }

    private CommentDetector lineCommentDetector() {
        return line -> commentStart.matcher(line).find();
    }
}
