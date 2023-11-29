package com.norcane.lysense.resource.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class PathMatcherTest {

    @Inject
    PathMatcher pathMatcher;

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "true |test          |test",
            "true |.test         |.test",
            "false|.test/jpg     |test/jpg",
            "false|test          |.test",
            "false|.test         |test",
            "true |t?st          |test",
            "true |??st          |test",
            "true |tes?          |test",
            "true |te??          |test",
            "true |?es?          |test",
            "false|tes?          |tes",
            "false|tes?          |testt",
            "false|tes?          |tsst",
            "true |*             |test",
            "true |test*         |test",
            "true |test*         |testTest",
            "true |*test*        |AnothertestTest",
            "true |*test         |Anothertest",
            "true |*/*           |test/test",
            "true |test*aaa      |testblaaaa",
            "false|test*         |tst",
            "false|test*         |tsttest",
            "false|*test*        |tsttst",
            "false|*test         |tsttst",
            "false|*/*           |tsttst",
            "false|test*aaa      |test",
            "false|test*aaa      |testblaaab",
            "true |.?            |.a",
            "true |.?.a          |.a.a",
            "true |.a.?          |.a.b",
            "true |.??.a         |.aa.a",
            "true |.a.??         |.a.bb",
            "true |.?            |.a",
            "true |.**           |.testing.testing",
            "true |.*.**         |.testing.testing",
            "true |.bla.**.bla   |.bla.testing.testing.bla",
            "true |.bla.**.bla   |.bla.testing.testing.bla.bla",
            "true |.**.test      |.bla.bla.test",
            "true |.bla.**.**.bla|.bla.bla.bla.bla.bla.bla",
            "true |.bla*bla.test |.blaXXXbla.test",
            "true |*bla.test     |.XXXbla.test",
            "false|.bla*bla.test |.blaXXXbl.test",
            "false|.*bla.test    |XXXblab.test",
            "false|.*bla.test    |XXXbl.test"
        }
    )
    void matches(boolean result, String pattern, String path) {
        assertEquals(result, pathMatcher.matches(pattern, path));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "true |t?st",
            "true |test*",
            "true |**.test",
            "false|.test/jpg",
            "false|test"
        }
    )
    void isPattern(boolean result, String pattern) {
        assertEquals(result, pathMatcher.isPattern(pattern));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "/WEB-INF/|/WEB-INF/*.xml",
            "/Users/john/|/Users/john/**/foo.xml",
            "/foo/bar|/foo/bar"
        }
    )
    void resolveRootPath(String expected, String path) {
        assertEquals(expected, pathMatcher.resolveRootPath(path));
    }
}