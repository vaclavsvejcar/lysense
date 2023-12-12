package com.norcane.lysense.resource.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.resource.Resource;

import static com.norcane.toolkit.Prelude.nonNull;

public class CannotReadResourceException extends ApplicationException {

    private final Resource resource;

    public CannotReadResourceException(Resource resource, Throwable cause) {
        super(ErrorCode.CANNOT_READ_RESOURCE, STR."Cannot read resource \{resource.uri()}", cause);

        this.resource = nonNull(resource);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Cannot read resource \{resource.uri()}")
            .solution(
                """
                    Very likely the given resource exists, but either might be a directory instead of a file,\
                    or the file isn't readable (missing privileges). Please check if the given path is correct."""
            )
            .build();
    }
}
