package com.norcane.lysense.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UnexpectedBehaviorExceptionTest {

    final UnexpectedBehaviorException exception = UnexpectedBehaviorException.wrap(new IllegalArgumentException("uh oh!"));
    final UnexpectedBehaviorException exceptionWithCause = UnexpectedBehaviorException.wrap(new IllegalArgumentException("uh oh!", new IOException()));

    @Test
    void errorDetail() {
        assertNotNull(exception.errorDetail());
    }

    @Test
    void getCause() {
        assertTrue(exceptionWithCause.getCause() instanceof IOException);
    }
}