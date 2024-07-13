/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.norcane.lysense.exception;

import com.norcane.lysense.meta.ProductInfo;

/**
 * Wrapper exception for unexpected exceptions that were caught and might signal some bug in the implementation.
 */
public class UnexpectedBehaviorException extends ApplicationException {

    private UnexpectedBehaviorException(final String message) {
        super(ErrorCode.UNEXPECTED_BEHAVIOR, "Unexpected error occurred during application execution: " + message);
    }

    private UnexpectedBehaviorException(final String message, final Throwable cause) {
        super(ErrorCode.UNEXPECTED_BEHAVIOR, "Unexpected error occurred during application execution: " + message, cause);
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
                """
                    Unexpected error occurred during application execution:

                    %s""".formatted(getCause())
            )
            .solution("This might be application bug. Please if possible, report it using the link below.")
            .seeAlsoLink(ProductInfo.URL_REPORT_BUG)
            .build();
    }
}
