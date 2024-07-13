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
package com.norcane.lysense.ui.exception;

import com.google.common.base.Throwables;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.ErrorDetail;
import com.norcane.lysense.meta.ProductInfo;
import com.norcane.lysense.ui.UIComponent;
import com.norcane.lysense.ui.console.Console;

import java.util.List;
import java.util.stream.Collectors;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * {@link UIComponent} that prints given {@link ApplicationExceptionPrinter} to end-user in human-friendly manner, explaining what possibly happened and what
 * could be possibly done to fix the problem. It can also be configured to display the full stack trace for debugging purposes.
 */
public class ApplicationExceptionPrinter implements UIComponent {

    private final boolean printStackTrace;
    private final ApplicationException exception;

    /**
     * Constructs new instance that will print given exception to end user, with optionally also printing the stack trace for debugging purposes.
     *
     * @param printStackTrace whether to print stack trace as well
     * @param exception       exception to print
     */
    private ApplicationExceptionPrinter(boolean printStackTrace, ApplicationException exception) {
        this.printStackTrace = printStackTrace;
        this.exception = exception;
    }

    public static ApplicationExceptionPrinter of(boolean printStackTrace, ApplicationException exception) {
        return new ApplicationExceptionPrinter(printStackTrace, nonNull(exception));
    }

    @Override
    public void render(Console console) {
        final ErrorDetail errorDetails = exception.errorDetail();
        final List<String> providedLinks = errorDetails.seeAlsoLinks();
        final List<String> linksToDisplay = providedLinks.isEmpty() ? List.of(ProductInfo.URL_HOMEPAGE) : providedLinks;
        final String listOfLinks = linksToDisplay.stream().map("  - @|underline %s|@"::formatted).collect(Collectors.joining("\n"));

        final String baseMessage =
            """

                @|bold,underline Problem:|@
                %s

                @|bold,underline Possible Solution:|@
                %s

                @|bold,underline See Also:|@
                %s
                """.formatted(errorDetails.problem(), errorDetails.solution(), listOfLinks);

        console.printLn(printStackTrace ? baseMessage + stackTrace() : baseMessage);

    }

    private String stackTrace() {
        return """

            @|bold,underline Stack Trace:|@
            %s
            """.formatted(Throwables.getStackTraceAsString(exception));
    }
}
