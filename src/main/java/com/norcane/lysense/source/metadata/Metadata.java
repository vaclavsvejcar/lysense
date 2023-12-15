package com.norcane.lysense.source.metadata;

import com.norcane.lysense.source.HeaderDetectionRules;
import com.norcane.lysense.source.comment.CommentDetector;
import com.norcane.lysense.source.comment.CommentDetectorFactory;
import com.norcane.toolkit.state.Memoized;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Represents <i>metadata</i> extracted from the source code. Contains details such as position of license header (if found) or possible candidate position for
 * newly generated license header.
 */
public class Metadata {

    private final List<Line> lines;
    private final Memoized<LicenseHeader> header = Memoized.detached();
    private final Memoized<HeaderCandidate> headerCandidate = Memoized.detached();

    private Metadata(List<Line> lines) {
        this.lines = lines;
    }

    /**
     * Returns <i>builder</i> used to construct new instance of {@link Metadata}.
     *
     * @param commentDetectorFactory comment detector factory
     * @param headerDetectionRules license header detection rules
     * @return builder instance
     */
    public static Builder builder(CommentDetectorFactory commentDetectorFactory, HeaderDetectionRules headerDetectionRules) {
        return new Builder(commentDetectorFactory, headerDetectionRules);
    }

    /**
     * Returns license header if any existing one found in source code.
     *
     * @return license header
     */
    public Optional<LicenseHeader> header() {
        return Optional.ofNullable(header.computeIfAbsent(() -> {
            final int putAfterIndex = putAfterIndex().orElse(0);
            final int putBeforeIndex = putBeforeIndex().orElse(lines.size());

            final List<Line> headerLines = lines.stream()
                .skip(putAfterIndex)
                .limit(putBeforeIndex - putAfterIndex)
                .dropWhile(line -> line.type() != LineType.COMMENT)
                .takeWhile(line -> line.type() == LineType.COMMENT)
                .toList();

            if (headerLines.isEmpty()) {
                return null;
            }

            final List<String> headerContent = headerLines.stream().map(Line::content).toList();
            final int startLineIndex = headerLines.getFirst().lineIndex();
            final int endLineIndex = headerLines.getLast().lineIndex();
            final int blankLinesAfter = blankLinesAfter(endLineIndex + 1);
            final int blankLinesBefore = blankLinesBefore(startLineIndex);

            return new LicenseHeader(startLineIndex + 1, endLineIndex + 1, blankLinesBefore, blankLinesAfter, headerContent);
        }));

    }

    /**
     * Returns license header candidate location to determine where to put newly generated license header.
     *
     * @return license header candidate
     */
    public HeaderCandidate headerCandidate() {
        return headerCandidate.computeIfAbsent(() -> {
            final Optional<Integer> putAfterIndex = putAfterIndex();
            final int blankLinesAfter = blankLinesAfter(putAfterIndex.map(i -> i + 1).orElse(0));

            return new HeaderCandidate(putAfterIndex.map(i -> i + 1).orElse(0), blankLinesAfter);

        });
    }

    Optional<Integer> putAfterIndex() {
        return lines.stream()
            .filter(line -> line.type() == LineType.PUT_AFTER_PATTERN)
            .findFirst()
            .map(Line::lineIndex);
    }

    Optional<Integer> putBeforeIndex() {
        return lines.stream()
            .filter(line -> line.type() == LineType.PUT_BEFORE_PATTERN)
            .reduce((first, _) -> first)
            .map(Line::lineIndex);
    }

    private int blankLinesAfter(int lineIndex) {
        return lines.stream()
            .skip(lineIndex)
            .takeWhile(line -> line.type() == LineType.BLANK)
            .toList()
            .size();
    }

    private int blankLinesBefore(int lineIndex) {
        return lines.stream()
            .limit(lineIndex)
            .sorted(Comparator.comparing(Line::lineIndex).reversed())
            .takeWhile(line -> line.type() == LineType.BLANK)
            .toList()
            .size();
    }

    /**
     * <i>Builder</i> class to construct new instance of {@link Metadata}.
     */
    public static class Builder {
        private final CommentDetector commentDetector;
        private final HeaderDetectionRules headerDetectionRules;
        private final List<Line> lines;

        public Builder(CommentDetectorFactory commentDetectorFactory,
                       HeaderDetectionRules headerDetectionRules) {

            this.commentDetector = nonNull(commentDetectorFactory).create();
            this.headerDetectionRules = nonNull(headerDetectionRules);
            this.lines = new ArrayList<>();
        }

        /**
         * Adds source code line that will be analyzed for metadata.
         *
         * @param line source code line
         * @return builder instance
         */
        public Builder addLine(String line) {

            if (line == null || line.isBlank()) {
                return registerLine(LineType.BLANK, null);
            }

            if (headerDetectionRules.isPutAfter(line)) {
                return registerLine(LineType.PUT_AFTER_PATTERN, null);
            }

            if (headerDetectionRules.isPutBefore(line)) {
                return registerLine(LineType.PUT_BEFORE_PATTERN, null);
            }

            if (commentDetector.isComment(line)) {
                return registerLine(LineType.COMMENT, line);
            }

            return registerLine(LineType.OTHER, null);
        }

        /**
         * Constructs new instance of {@link Metadata}.
         *
         * @return new instance
         */
        public Metadata build() {
            return new Metadata(lines);
        }

        private Builder registerLine(LineType lineType, String line) {
            lines.add(new Line(lines.size(), lineType, line));
            return this;
        }
    }

    private record Line(int lineIndex, LineType type, String content) {
    }

    private enum LineType {
        BLANK,
        COMMENT,
        PUT_AFTER_PATTERN,
        PUT_BEFORE_PATTERN,
        OTHER
    }
}
