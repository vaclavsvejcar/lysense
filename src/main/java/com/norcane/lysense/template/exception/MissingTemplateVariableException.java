package com.norcane.lysense.template.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;

/**
 * Thrown when a template variable is missing in the configuration file.
 */
public class MissingTemplateVariableException extends ApplicationException {

    private final String templateName;
    private final String variableName;

    public MissingTemplateVariableException(String templateName, String variableName) {
        super(ErrorCode.MISSING_TEMPLATE_VARIABLE, STR."Missing variable '\{variableName}' for template '\{templateName}'");

        this.templateName = templateName;
        this.variableName = variableName;
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(STR."Template '\{templateName}' contains placeholder with name '\{variableName}', but no such variable was provided.")
            .solution(STR."Please check if you defined variable '\{variableName}' in the configuration file.")
            .build();
    }
}
