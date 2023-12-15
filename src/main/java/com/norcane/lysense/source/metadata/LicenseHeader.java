package com.norcane.lysense.source.metadata;

import java.util.List;

/**
 * Represents existing license header found in source code. Note that lines are numbered from {@code 1}.
 *
 * @param startLine        start line of the license header
 * @param endLine          end line of the license header
 * @param blankLinesBefore blank lines before the start of the license header
 * @param blankLinesAfter  blank lines after the end of the license header
 * @param lines            lines of the license header
 */
public record LicenseHeader(int startLine, int endLine, int blankLinesBefore, int blankLinesAfter, List<String> lines) {
}
