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
package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

import static com.norcane.toolkit.Prelude.nonNull;
import static java.util.FormatProcessor.FMT;

public class InvalidConfigurationException extends ApplicationException {

    private final Set<ConstraintViolation<Configuration>> violations;

    public InvalidConfigurationException(Set<ConstraintViolation<Configuration>> violations) {
        super(ErrorCode.INVALID_CONFIGURATION, FMT."Invalid configuration, found %d\{violations.size()} problems");

        this.violations = nonNull(violations);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
                .problem(
                        STR."""
                       Provided application config source is invalid and has following issues:

                       \{listOfViolations()}
                       """.strip()
                )
                .solution("Please check the error messages above and correct the configuration appropriately.")
                .build();
    }

    private String listOfViolations() {
        return violations
                .stream()
                .map(violation -> STR."  - @|bold,underline \{violation.getPropertyPath()}|@ @|bold \{violation.getMessage()}|@")
                .collect(Collectors.joining("\n"));
    }
}
