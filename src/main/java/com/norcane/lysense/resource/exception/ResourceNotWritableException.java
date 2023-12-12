package com.norcane.lysense.resource.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.resource.Resource;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Thrown when a resource is not writable.
 */
public class ResourceNotWritableException extends ApplicationException {

    private final Resource resource;

    public ResourceNotWritableException(Resource resource) {
        super(ErrorCode.RESOURCE_NOT_WRITABLE, STR."Resource \{resource} is not writable");

        this.resource = nonNull(resource);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Resource \{resource} is not writable")
            .solution(
                """
                    It's likely you used non-writable kind of resource (classpath, HTTP(S)) in place where writable resource
                    (file system) is required."""
            )
            .build();
    }
}
