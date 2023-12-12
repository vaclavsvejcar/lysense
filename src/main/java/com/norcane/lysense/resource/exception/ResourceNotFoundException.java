package com.norcane.lysense.resource.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

import java.net.URI;

import static com.norcane.toolkit.Prelude.nonNull;

public class ResourceNotFoundException extends ApplicationException {

    private final String location;

    public ResourceNotFoundException(String location) {
        super(ErrorCode.RESOURCE_NOT_FOUND, STR."Resource not found: \{location}");

        this.location = location;
    }

    public ResourceNotFoundException(URI uri) {
        super(ErrorCode.RESOURCE_NOT_FOUND, STR."Resource not found: \{uri}");

        this.location = uri.toString();
    }

    public ResourceNotFoundException(String location, Throwable cause) {
        super(ErrorCode.RESOURCE_NOT_FOUND, STR."Resource not found: \{location}", cause);

        this.location = nonNull(location);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Resource not found: \{location}")
            .solution("Please check if given resource exists.")
            .build();
    }
}
