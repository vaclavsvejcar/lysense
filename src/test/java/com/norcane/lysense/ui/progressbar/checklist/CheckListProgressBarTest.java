package com.norcane.lysense.ui.progressbar.checklist;


import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.ProgressBar;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CheckListProgressBarTest {

    @InjectMock
    Console console;

    @Test
    void currentAndStep() {
        final ProgressBar progressBar = new CheckListProgressBar();

        assertEquals(0, progressBar.current());
        progressBar.step("Another step");
        assertEquals(1, progressBar.current());
    }

    @Test
    void render_interactiveConsole() {
        final ProgressBar progressBar = new CheckListProgressBar();

        // -- mocks
        when(console.isInteractive()).thenReturn(true);

        progressBar.step("foo");
        progressBar.render(console);

        progressBar.step("bar");
        progressBar.render(console);

        progressBar.cleanup(console);

        // -- captors
        final var consolePrintCaptor = ArgumentCaptor.forClass(String.class);
        final var consolePrintLnCaptor = ArgumentCaptor.forClass(String.class);

        // -- verify
        verify(console, times(4)).isInteractive();
        verify(console, times(2)).print(consolePrintCaptor.capture());
        verify(console, times(2)).printLn(consolePrintLnCaptor.capture());
        verify(console, times(2)).clearLine();

        // -- assertions
        final List<String> printValues = consolePrintCaptor.getAllValues();
        final List<String> printLnValues = consolePrintLnCaptor.getAllValues();
        assertEquals(List.of("@|bold [ ]|@ foo", "@|bold [ ]|@ bar"), printValues);
        assertEquals(List.of("@|bold [|@@|bold,green ✓|@@|bold ]|@ foo", "@|bold [|@@|bold,green ✓|@@|bold ]|@ bar"), printLnValues);
    }

    @Test
    void render_nonInteractiveConsole() {
        final ProgressBar progressBar = new CheckListProgressBar();

        // -- mocks
        when(console.isInteractive()).thenReturn(false);

        progressBar.step("foo");
        progressBar.render(console);

        progressBar.step("bar");
        progressBar.render(console);

        progressBar.cleanup(console);

        // -- captors
        final var consolePrintCaptor = ArgumentCaptor.forClass(String.class);
        final var consolePrintLnCaptor = ArgumentCaptor.forClass(String.class);

        // -- verify
        verify(console, times(4)).isInteractive();
        verify(console, times(2)).print(consolePrintCaptor.capture());
        verify(console, times(2)).printLn(consolePrintLnCaptor.capture());
        verify(console, never()).clearLine();

        // -- assertions
        final List<String> printValues = consolePrintCaptor.getAllValues();
        final List<String> printLnValues = consolePrintLnCaptor.getAllValues();
        assertEquals(List.of("foo...", "bar..."), printValues);
        assertEquals(List.of(" DONE", " DONE"), printLnValues);
    }
}
