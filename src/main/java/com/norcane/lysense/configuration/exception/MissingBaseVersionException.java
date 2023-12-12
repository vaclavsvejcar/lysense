package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.resource.Resource;

import static com.norcane.toolkit.Prelude.nonNull;

public class MissingBaseVersionException extends ApplicationException {

    private final Resource resource;

    public MissingBaseVersionException(Resource resource) {
        super(ErrorCode.MISSING_BASE_VERSION, STR."Cannot parse base version from configuration '\{resource}'");

        this.resource = nonNull(resource);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Cannot parse base version from configuration '\{resource}'")
            .solution("Please make sure that the info about minimum compatible version is present in the configuration file.")
            .build();
    }
}
