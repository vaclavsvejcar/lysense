package com.norcane.lysense.cli;

/**
 * Represents return code of the application.
 */
public enum ReturnCode {

    /**
     * Successful execution.
     */
    SUCCESS(0),

    /**
     * Error during execution.
     */
    ERROR(1);

    private final int code;

    ReturnCode(int code) {
        this.code = code;
    }

    /**
     * Returns the return code.
     *
     * @return return code
     */
    public int code() {
        return code;
    }
}
