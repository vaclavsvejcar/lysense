package com.norcane.lysense.source;

/**
 * Result of source code modification.
 */
public enum SourceModificationResult {

    /**
     * Source code was modified.
     */
    MODIFIED(true),

    /**
     * Source code was not modified.
     */
    NOT_MODIFIED(false);

    private final boolean modified;

    SourceModificationResult(boolean modified) {
        this.modified = modified;
    }

    public boolean modified() {
        return modified;
    }
}
