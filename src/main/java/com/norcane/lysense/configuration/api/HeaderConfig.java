package com.norcane.lysense.configuration.api;

/**
 * Defines configuration for the license header for selected programming language.
 */
public interface HeaderConfig {

    /**
     * Defines style of the license header (block or line comment).
     *
     * @return style of the license header
     */
    HeaderStyle headerStyle();

    /**
     * Defines spacing in number of blank lines that should be preserved before/after the license header.
     *
     * @return spacing in number of blank lines that should be preserved before/after the license header
     */
    HeaderSpacing headerSpacing();
}
