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
     * @param exception exception to print
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
        final String listOfLinks = linksToDisplay.stream().map(link -> STR."  - @|underline \{link}|@").collect(Collectors.joining("\n"));

        final String baseMessage =
            STR."""

                @|bold,underline Problem:|@
                \{errorDetails.problem()}

                @|bold,underline Possible Solution:|@
                \{errorDetails.solution()}

                @|bold,underline See Also:|@
                \{listOfLinks}
                """;

        console.printLn(printStackTrace ? baseMessage + stackTrace() : baseMessage);

    }

    private String stackTrace() {
        return STR."""

            @|bold,underline Stack Trace:|@
            \{Throwables.getStackTraceAsString(exception)}
            """;
    }
}
