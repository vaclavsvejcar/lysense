package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

import java.net.URI;

import static com.norcane.toolkit.Prelude.nonNull;

public class NoConfigurationFoundException extends ApplicationException {

    private final URI location;

    public NoConfigurationFoundException(URI location) {
        super(ErrorCode.NO_CONFIGURATION_FOUND, STR."No configuration file found: \{location}");

        this.location = nonNull(location);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem("No valid configuration file found.")
            .solution(
                STR."""
                    Configuration file is expected to exist at:

                    @|bold \{location}|@

                    Please create one or check that existing one is named correctly.""".strip()
            )
            .build();
    }
}
