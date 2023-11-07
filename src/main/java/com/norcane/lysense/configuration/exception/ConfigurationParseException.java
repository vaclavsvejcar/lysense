package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.resource.Resource;

import static com.norcane.toolkit.Prelude.nonNull;

public class ConfigurationParseException extends ApplicationException {

    private final Resource resource;
    private final Throwable cause;

    public ConfigurationParseException(Resource resource, Throwable cause) {
        super(ErrorCode.ERROR_PARSING_CONFIGURATION, STR. "Error loading configuration from '\{ resource }': \{ cause.getMessage() }" , cause);

        this.resource = nonNull(resource);
        this.cause = nonNull(cause);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR. "Error loading configuration from '\{ resource }': \{ cause.getMessage() }" )
            .solution(
                """
                    Please check that some of the following isn't wrong:
                                
                      - syntax of the configuration file is invalid
                      - you don't have enough right to access the configuration file"""
            )
            .build();
    }
}
