package com.norcane.lysense.source.variables;

import com.norcane.lysense.template.Variables;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class PatternVariablesExtractorTest {

    @Test
    void extract() {
        final Variables.Variable expected = new Variables.Variable("_java.package_name", "foo.bar");
        final VariablesExtractor extractor = PatternVariablesExtractor.from(Map.of(
            "_java.package_name", Pattern.compile("^package (.*);$")
        ));

        assertEquals(Optional.of(expected), extractor.extract("package foo.bar;"));
    }

    @Test
    void extract_noop() {
        final VariablesExtractor extractor = VariablesExtractor.noExtraction();

        assertEquals(Optional.empty(), extractor.extract("package foo.bar;"));
    }
}