package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

import static com.norcane.toolkit.Prelude.nonNull;

public class HeaderConfigNotFoundException extends ApplicationException {

    private final String name;

    public HeaderConfigNotFoundException(String name) {
        super(ErrorCode.HEADER_CONFIG_NOT_FOUND, STR."Header configuration for type '\{name}' not found");

        this.name = nonNull(name);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Header configuration for type '\{name}' not found")
            .solution(STR."Please check that your configuration file contains header configuration for type '\{name}'")
            .build();
    }

}
