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
