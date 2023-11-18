package com.norcane.lysense.cli.command;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.lysense.ui.console.Console;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class AbstractCommandTest {

    @InjectMock
    Console console;

    @Test
    void run() {
        final TestCommand command = new TestCommand(console);

        command.call();
        assertTrue(command.executed);
    }

    @Test
    void printProductHeader() {
        final AbstractCommand command = new TestCommand(console);

        command.printProductHeader();
    }


    static class TestCommand extends AbstractCommand {

        boolean executed = false;

        public TestCommand(Console console) {
            super(console);
        }

        @Override
        protected ReturnCode execute() {
            executed = true;
            throw new ResourceNotFoundException("testing exception handling");
        }
    }

}