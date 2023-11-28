package com.norcane.lysense.resource.exception;


import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.resource.Resource;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Thrown when an error occurs while writing to a resource.
 */
public class CannotWriteResourceException extends ApplicationException {

    private final Resource resource;

    public CannotWriteResourceException(Resource resource, Throwable cause) {
        super(ErrorCode.CANNOT_WRITE_RESOURCE, STR. "Cannot write resource \{ resource.uri() }" , cause);

        this.resource = nonNull(resource);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR. "Cannot write resource \{ resource.uri() }" )
            .solution("Please check if target resource exists and you have write privileges.")
            .build();
    }
}
