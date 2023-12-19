package com.norcane.lysense.resource.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

import java.net.URI;

/**
 * Exception thrown when resource is not found.
 */
public class ResourceNotFoundException extends ApplicationException {

    private final URI location;

    /**
     * Creates new instance of {@link ResourceNotFoundException}.
     *
     * @param uri URI of the resource that was not found
     */
    public ResourceNotFoundException(URI uri) {
        super(ErrorCode.RESOURCE_NOT_FOUND, STR."Resource not found: \{uri}");

        this.location = uri;
    }

    /**
     * Returns the location of the resource that was not found.
     *
     * @return location uri
     */
    public URI location() {
        return location;
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Resource not found: \{location}")
            .solution("Please check if given resource exists.")
            .build();
    }
}
