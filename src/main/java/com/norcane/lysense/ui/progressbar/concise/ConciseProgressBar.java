package com.norcane.lysense.ui.progressbar.concise;

import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.ProgressBar;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Implementation of {@link ProgressBar} that renders either single-line progressbar with spinner on <i>interactive terminal</i> or list of steps on
 * <i>non-interactive terminal</i>.
 *
 * <br><br><b>Example of output (interactive terminal)</b>
 * <pre>
 * - [1 of 301] Formatting file foo.txt
 * </pre>
 *
 * <b>Example of output (non-interactive terminal)</b>
 * <pre>
 * [1 of 301] Formatting file foo.txt
 * [2 of 301] Formatting file bar.txt
 * ...
 * </pre>
 */
public class ConciseProgressBar implements ProgressBar {

    private static final char[] SPINNER_FRAMES = new char[]{'-', '\\', '|', '/'};
    private static final int SPINNER_MODULO = SPINNER_FRAMES.length;

    private final int maximum;

    private int current;
    private String message;

    public ConciseProgressBar(int maximum, String initialMessage) {
        this.current = 0;
        this.maximum = maximum;
        this.message = nonNull(initialMessage);
    }

    @Override
    public void render(Console console) {
        final char spinner = SPINNER_FRAMES[current % SPINNER_MODULO];
        final String currentFormat = STR."%\{String.valueOf(maximum).length()}s";
        final boolean interactive = console.isInteractive();
        final String currentSpinner = interactive ? STR."@|bold,magenta \{spinner}|@ " : "";

        final String rendered = STR."\{currentSpinner}@|bold [\{currentFormat.formatted(current)} of \{maximum}]|@ \{message}";

        if (interactive) {
            console.clearLine();
            console.print(rendered);    // do not print newlines so updated progress bar can replace old
        } else {
            console.printLn(rendered);  // print every update on new line
        }
    }

    @Override
    public int current() {
        return this.current;
    }

    @Override
    public void step(String message) {
        this.current++;
        this.message = message;
    }

    @Override
    public void cleanup(Console console) {
        console.clearLine();
    }
}
