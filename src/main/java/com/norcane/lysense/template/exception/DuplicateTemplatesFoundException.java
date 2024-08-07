/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.norcane.lysense.template.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorCode;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.template.TemplateKey;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Thrown when multiple templates are found for same source file type.
 */
public class DuplicateTemplatesFoundException extends ApplicationException {
    private final TemplateKey templateKey;
    private final List<URI> uris;

    public DuplicateTemplatesFoundException(TemplateKey templateKey, List<URI> uris) {
        super(ErrorCode.DUPLICATE_TEMPLATES_FOUND, "Multiple templates found for template key '%s'".formatted(templateKey));

        this.templateKey = nonNull(templateKey);
        this.uris = nonNull(uris);
    }

    @Override
    public ErrorDetail errorDetail() {
        return ErrorDetail.builder()
            .problem(
                """
                    Template paths contain multiple templates for same template key @|bold %s|@:
                    %s
                    """.formatted(templateKey, listOfUris())
            )
            .solution("Make sure that only one template is present for selected template key")
            .build();
    }

    private String listOfUris() {
        return uris.stream()
            .map("  - @|bold %s|@"::formatted)
            .collect(Collectors.joining("\n"));
    }
}
