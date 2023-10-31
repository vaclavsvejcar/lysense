package com.norcane.lysense.exception;

public enum ErrorCode {

    CANNOT_READ_RESOURCE(623, "Cannot read resource");

    private final int code;
    private final String title;

    ErrorCode(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int code() {
        return code;
    }

    public String title() {
        return title;
    }
}
