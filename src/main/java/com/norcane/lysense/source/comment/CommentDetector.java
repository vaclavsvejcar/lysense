package com.norcane.lysense.source.comment;

/**
 * Detects presence of a comment in source code.
 */
public interface CommentDetector {

    /**
     * Check if the given line of source code is start, end or body of comment.
     *
     * @param line line of source code to check
     * @return {@code true} if the line is comment (or part of header in multi-line comments)
     */
    boolean isComment(String line);
}
