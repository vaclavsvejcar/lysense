package com.norcane.lysense.source.metadata;

import com.norcane.lysense.source.HeaderDetectionRules;
import com.norcane.lysense.source.comment.CommentDetectorFactory;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import io.quarkus.test.junit.QuarkusTest;

import static com.norcane.lysense.test.Assertions.assertIsPresent;
import static com.norcane.lysense.test.Assertions.assertNotPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class MetadataTest {

    static CommentDetectorFactory commentDetectorFactory = CommentDetectorFactory.blockSyntax(Pattern.compile("^/\\*(?!\\*)"), Pattern.compile("\\*/$"));
    static HeaderDetectionRules headerDetectionRules = HeaderDetectionRules.putBefore(Pattern.compile("^package"));
    static HeaderDetectionRules headerDetectionRulesPutAfter = HeaderDetectionRules.from(Pattern.compile("^PA"), Pattern.compile("^package"));

    @Test
    void header_noHeaderFound() {
        final String sample = """
            package foo;
                        
            /*
             * This is not header
             */
                        
            class Test {
                public static void main(String[] args) {
                    System.out.println("Hello!");
                }
            }
            """;

        final Metadata metadata = buildMetadata(sample);
        assertNotPresent(metadata.header());
    }

    @Test
    void header_headerFound() {
        final String sample = """
                        
            /*
             * This is header
             */
                        
                        
            package foo;
                        
            class Test {
                public static void main(String[] args) {
                    System.out.println("Hello!");
                }
            }
            """;
        final LicenseHeader expected = new LicenseHeader(2, 4, 1, 2, List.of("/*", " * This is header", " */"));

        final Metadata metadata = buildMetadata(sample);
        assertIsPresent(expected, metadata.header());
    }

    @Test
    void headerCandidate_noPutAfter_noSpaces() {
        final String sample = """
            foo bar
            """;

        final Metadata metadata = buildMetadata(sample);
        assertEquals(new HeaderCandidate(0, 0), metadata.headerCandidate());
    }

    @Test
    void headerCandidate_noPutAfter_hasSpaces() {
        final String sample = """
                        
            foo bar
            """;

        final Metadata metadata = buildMetadata(sample);
        assertEquals(new HeaderCandidate(0, 1), metadata.headerCandidate());
    }

    @Test
    void headerCandidate_putAfter_noSpaces() {
        final String sample = """
                        
            foo bar
                        
            PA
            hello
            """;

        final Metadata metadata = buildMetadataPutAfter(sample);
        assertEquals(new HeaderCandidate(4, 0), metadata.headerCandidate());
    }

    @Test
    void headerCandidate_putAfter_withSpaces() {
        final String sample = """
                        
            foo bar
                        
            PA
                        
            hello
            """;

        final Metadata metadata = buildMetadataPutAfter(sample);
        assertEquals(new HeaderCandidate(4, 1), metadata.headerCandidate());
    }

    @Test
    void putAfterIndex() {
        final String sample = """
                        
            foo
                        
            PA
                        
            PA
            """;

        final Metadata metadata = buildMetadataPutAfter(sample);
        assertIsPresent(3, metadata.putAfterIndex());
    }

    @Test
    void putBeforeIndex() {
        final String sample = """
                        
            foo
                        
            package foo;
                        
            package bar;
                        
            package baz;
            """;

        final Metadata metadata = buildMetadata(sample);
        assertIsPresent(3, metadata.putBeforeIndex());
    }

    private Metadata buildMetadata(String sample) {
        final Metadata.Builder builder = Metadata.builder(commentDetectorFactory, headerDetectionRules);
        Arrays.stream(sample.split("\n")).forEach(builder::addLine);

        return builder.build();
    }

    private Metadata buildMetadataPutAfter(String sample) {
        final Metadata.Builder builder = Metadata.builder(commentDetectorFactory, headerDetectionRulesPutAfter);
        Arrays.stream(sample.split("\n")).forEach(builder::addLine);

        return builder.build();
    }
}