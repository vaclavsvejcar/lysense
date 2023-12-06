package com.norcane.lysense.configuration.api;

/**
 * Defines spacing in number of blank lines that should be preserved before/after the license header.
 */
public interface HeaderSpacing {

    /**
     * Number of blank lines after the license header.
     *
     * @return number of blank lines after the license header
     */
    Integer blankLinesAfter();

    /**
     * Number of blank lines before the license header.
     *
     * @return number of blank lines before the license header
     */
    Integer blankLinesBefore();
}
