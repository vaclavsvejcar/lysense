package com.norcane.lysense.exception;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Represents top level of the application exception hierarchy. This exception is meant to be caught during the program execution and displayed to end-user in
 * user-friendly form, not only showing what is the problem, but also what possible solutions can be done to fix the problem. Each implementing exception is
 * required to define its own unique <i>error code</i> that serves as easy way for end-user to quickly search online for the particular exception in
 * <i>Norcane Error Index</i>.
 *
 * @see ErrorCode
 * @see ErrorDetail
 */
public abstract class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode, String message) {
        super(message);

        this.errorCode = nonNull(errorCode);
    }

    public ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);

        this.errorCode = nonNull(errorCode);
    }

    public abstract ErrorDetail errorDetail();

    public ErrorCode errorCode() {
        return errorCode;
    }
}
