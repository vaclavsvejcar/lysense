package com.norcane.lysense.resource.util;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.inline.InlineResource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static com.norcane.lysense.test.Assertions.assertNotPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LineSeparatorTest {

    @Test
    void from() {
        assertIsPresent(LineSeparator.CR, LineSeparator.from("\r"));
        assertIsPresent(LineSeparator.LF, LineSeparator.from("\n"));
        assertIsPresent(LineSeparator.CRLF, LineSeparator.from("\r\n"));
        assertNotPresent(LineSeparator.from("foo"));
    }

    @ParameterizedTest
    @MethodSource("detectArguments")
    void detect(Resource resource, LineSeparator expected) {
        assertEquals(expected, LineSeparator.detect(resource).orElse(null));
    }

    private static Stream<Arguments> detectArguments() {
        return Stream.of(
            Arguments.of(InlineResource.of("Hello, world!"), null),
            Arguments.of(InlineResource.of("one\rtwo\rthree"), LineSeparator.CR),
            Arguments.of(InlineResource.of("one\ntwo\nthree"), LineSeparator.LF),
            Arguments.of(InlineResource.of("one\r\ntwo\r\nthree"), LineSeparator.CRLF)
        );
    }

}