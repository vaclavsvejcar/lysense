package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.configuration.domain.Configuration;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;

import static com.norcane.toolkit.Prelude.nonNull;

public class InvalidConfigurationException extends ApplicationException {

    private final Set<ConstraintViolation<Configuration>> violations;

    public InvalidConfigurationException(Set<ConstraintViolation<Configuration>> violations) {
        super(ErrorCode.INVALID_CONFIGURATION, "Invalid configuration, found %d problems".formatted(violations.size()));

        this.violations = nonNull(violations);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(
                STR. """
                       Provided application config source is invalid and has following issues:

                       \{ listOfViolations() }
                       """ .strip()
            )
            .solution("Please check the error messages above and correct the configuration appropriately.")
            .build();
    }

    private String listOfViolations() {
        return violations
            .stream()
            .map(violation -> STR. "  - @|bold,underline \{ violation.getPropertyPath() }|@ @|bold \{ violation.getMessage() }|@" )
            .collect(Collectors.joining("\n"));
    }
}
