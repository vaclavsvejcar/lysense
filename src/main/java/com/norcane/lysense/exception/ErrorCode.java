package com.norcane.lysense.exception;

public enum ErrorCode {

    CANNOT_READ_RESOURCE(623, "Cannot read resource"),
    CANNOT_WRITE_RESOURCE(793, "Cannot write resource"),
    DUPLICATE_TEMPLATES_FOUND(347, "Duplicate templates found"),
    ERROR_PARSING_CONFIGURATION(207, "Error parsing configuration"),
    HEADER_CONFIG_NOT_FOUND(341, "Header configuration not found"),
    INCOMPATIBLE_CONFIGURATION(173, "Incompatible configuration"),
    INVALID_CONFIGURATION(571, "Invalid configuration"),
    MISSING_BASE_VERSION(222, "Missing base version"),
    MISSING_TEMPLATE_VARIABLE(374, "Missing template variable"),
    NO_CONFIGURATION_FOUND(370, "No configuration found"),
    RESOURCE_NOT_FOUND(45, "Resource not found"),
    RESOURCE_NOT_WRITABLE(242, "Resource not writable"),
    UNEXPECTED_BEHAVIOR(339, "Unexpected behavior");

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
