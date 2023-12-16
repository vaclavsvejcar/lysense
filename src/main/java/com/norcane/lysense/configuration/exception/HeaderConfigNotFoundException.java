package com.norcane.lysense.configuration.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.source.LanguageId;

import static com.norcane.toolkit.Prelude.nonNull;

public class HeaderConfigNotFoundException extends ApplicationException {

    private final LanguageId languageId;

    public HeaderConfigNotFoundException(LanguageId languageId) {
        super(ErrorCode.HEADER_CONFIG_NOT_FOUND, STR."Header configuration for language '\{languageId}' not found");

        this.languageId = nonNull(languageId);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Header configuration for language '\{languageId}' not found")
            .solution(STR."Please check that your configuration file contains header configuration for language '\{languageId}'")
            .build();
    }

}
