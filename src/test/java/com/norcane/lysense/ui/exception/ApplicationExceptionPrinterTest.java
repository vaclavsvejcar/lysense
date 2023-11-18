package com.norcane.lysense.ui.exception;

import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.exception.UnexpectedBehaviorException;
import com.norcane.lysense.ui.console.Console;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ApplicationExceptionPrinterTest {

    @Inject
    Console console;

    @Test
    void render() {
        final ApplicationException exception = UnexpectedBehaviorException.wrap(new IllegalArgumentException("Uh oh!", new IOException()));
        console.render(ApplicationExceptionPrinter.of(true, exception));
    }
}