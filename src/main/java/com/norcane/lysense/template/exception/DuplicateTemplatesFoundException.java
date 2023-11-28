package com.norcane.lysense.template.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Thrown when multiple templates are found for same source file type.
 */
public class DuplicateTemplatesFoundException extends ApplicationException {
    private final String type;
    private final List<URI> uris;

    public DuplicateTemplatesFoundException(String type, List<URI> uris) {
        super(ErrorCode.DUPLICATE_TEMPLATES_FOUND, STR. "Multiple templates found for source file type '\{ type }'" );

        this.type = nonNull(type);
        this.uris = nonNull(uris);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(
                STR. """
                    Template paths contain multiple templates for same source file type @|bold \{ type }|@:
                    \{ listOfUris() }
                    """
            )
            .solution("Make sure that only one template is present for selected file type")
            .build();
    }

    private String listOfUris() {
        return uris.stream()
            .map(uri -> STR. "  - @|bold \{ uri }|@" )
            .collect(Collectors.joining("\n"));
    }
}
