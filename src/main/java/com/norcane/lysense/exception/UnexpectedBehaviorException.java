package com.norcane.lysense.exception;

import com.norcane.lysense.meta.ProductInfo;

/**
 * Wrapper exception for unexpected exceptions that were caught and might signal some bug in the implementation.
 */
public class UnexpectedBehaviorException extends ApplicationException {

    private UnexpectedBehaviorException(final String message) {
        super(ErrorCode.UNEXPECTED_BEHAVIOR, STR."Unexpected error occurred during application execution: \{message}");
    }

    private UnexpectedBehaviorException(final String message, final Throwable cause) {
        super(ErrorCode.UNEXPECTED_BEHAVIOR, STR."Unexpected error occurred during application execution: \{message}", cause);
    }

    /**
     * Wraps given {@link Throwable} into {@link UnexpectedBehaviorException}, using its message as the error message and its cause as the cause.
     */
    public static UnexpectedBehaviorException wrap(final Throwable cause) {
        return cause.getCause() != null
               ? new UnexpectedBehaviorException(cause.getMessage(), cause.getCause())
               : new UnexpectedBehaviorException(cause.getMessage());
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(
                STR."""
                    Unexpected error occurred during application execution:

                    \{getCause()}"""
            )
            .solution("This might be application bug. Please if possible, report it using the link below.")
            .seeAlsoLink(ProductInfo.URL_REPORT_BUG)
            .build();
    }
}
