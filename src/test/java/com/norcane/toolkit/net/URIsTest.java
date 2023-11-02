package com.norcane.toolkit.net;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class URIsTest {

    @ParameterizedTest
    @CsvSource({"foo:bar,foo:bar", "foo:ba r,foo:ba%20r"})
    void create(String sample, String expected) {
        assertEquals(expected, URIs.create(sample).toString());
    }

    @ParameterizedTest
    @CsvSource({"foo:bar,foo:bar", "foo:ba r,foo:ba%20r"})
    void escape(String sample, String expected) {
        assertEquals(expected, URIs.escape(sample));
    }
}