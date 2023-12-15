package com.norcane.lysense.source.comment;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class CommentDetectorFactoryTest {

    static final Pattern PATTERN_BLOCK_START = Pattern.compile("^/\\*");
    static final Pattern PATTERN_BLOCK_END = Pattern.compile("\\*/$");
    static final Pattern PATTERN_LINE = Pattern.compile("^//");

    @Test
    void isHeader_block_multiLine() {
        final CommentDetector detector = CommentDetectorFactory.blockSyntax(PATTERN_BLOCK_START, PATTERN_BLOCK_END).create();

        assertFalse(detector.isComment(""));
        assertFalse(detector.isComment("functionCall();"));
        assertTrue(detector.isComment("/* header starts"));
        assertTrue(detector.isComment("header body"));
        assertTrue(detector.isComment("header ends */"));
        assertFalse(detector.isComment("after header"));
        assertTrue(detector.isComment("/* block comment but not header */"));
    }

    @Test
    void isHeader_block_singleLine() {
        final CommentDetector detector = CommentDetectorFactory.blockSyntax(PATTERN_BLOCK_START, PATTERN_BLOCK_END).create();

        assertFalse(detector.isComment(""));
        assertFalse(detector.isComment("functionCall();"));
        assertTrue(detector.isComment("/* single line block header */"));
        assertFalse(detector.isComment("after header"));
        assertTrue(detector.isComment("/* block comment but not header */"));
    }

    @Test
    void isHeader_line() {
        final CommentDetector detector = CommentDetectorFactory.lineSyntax(PATTERN_LINE).create();

        assertFalse(detector.isComment("some.code();"));
        assertFalse(detector.isComment("some // line comment but not header"));
        assertTrue(detector.isComment("// line header starts"));
        assertTrue(detector.isComment("// continues here"));
        assertTrue(detector.isComment("// and ends here"));
        assertFalse(detector.isComment("some other code"));
        assertTrue(detector.isComment("// line comment but not header"));
    }

}