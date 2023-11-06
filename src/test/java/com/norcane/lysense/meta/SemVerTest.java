package com.norcane.lysense.meta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class SemVerTest {

    @Test
    void from_illegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> SemVer.from("foo"));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            "0.1.0    |0|1|0|",
            "0.1.0-RC3|0|1|0|RC3"
        })
    void from(String raw, int major, int minor, int patch, String suffix) {
        assertEquals(new SemVer(major, minor, patch, suffix), SemVer.from(raw));
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = {
            " 0|0.1.0    |0.1.0",
            " 1|0.1.0    |0.1.0-RC3",
            "-1|0.1.0-RC2|0.1.0-RC3",
            "-1|0.1.0    |0.1.1",
            " 1|1.0.1    |0.2.1",
            " 1|1.1.0    |1.0.0",
            "-1|1.0.0-RC1|1.0.0",
            " 1|1.0.0-2  |1.0.0-1"
        })
    void compareTo(int result, String left, String right) {
        assertEquals(result, SemVer.from(left).compareTo(SemVer.from(right)));

    }

    @ParameterizedTest
    @CsvSource({"0.1.0", "0.1.0-RC3"})
    void testToString(String raw) {
        assertEquals(raw, SemVer.from(raw).toString());
    }
}