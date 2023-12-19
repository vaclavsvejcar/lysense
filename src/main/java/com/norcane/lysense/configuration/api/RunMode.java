package com.norcane.lysense.configuration.api;

/**
 * Mode of the <i>run command</i> of the application. Defines how should be existing license headers handled.
 */
public enum RunMode {

    /**
     * Add license headers to files that don't have them, don't touch existing ones.
     */
    ADD,

    /**
     * Remove license headers from files that have them, don't touch files that don't have them.
     */
    DROP,

    /**
     * Update license headers in files that have them or add them to files that don't have them.
     */
    UPDATE
}
