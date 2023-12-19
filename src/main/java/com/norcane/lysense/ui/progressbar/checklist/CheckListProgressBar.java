package com.norcane.lysense.ui.progressbar.checklist;

import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.ProgressBar;

/**
 * Implementation of {@link ProgressBar} that renders as a <i>checklist</i>. Exact appearance depends on whether current terminal is <i>interactive</i> or not.
 *
 * <br><br><b>Example of output (interactive terminal)</b>
 * <pre>
 * [✓] Done task
 * [✓] Also done task
 * [ ] Current task
 * </pre>
 *
 * <b>Example of output (non-interactive terminal)</b>
 * <pre>
 * Done task... DONE
 * Also done task... DONE
 * Current task...
 * </pre>
 *
 * @see Console#isInteractive()
 */
public class CheckListProgressBar implements ProgressBar {

    private int current;
    private String message;
    private String previousMessage;

    public CheckListProgressBar() {
        this.current = 0;
        this.message = null;
        this.previousMessage = null;
    }

    @Override
    public void render(Console console) {
        markPreviousAsDone(console);
        renderCurrent(console);
    }

    @Override
    public int current() {
        return current;
    }

    @Override
    public void step(String message) {
        this.current++;
        this.previousMessage = this.message;
        this.message = message;
    }

    @Override
    public void cleanup(Console console) {
        this.previousMessage = this.message;
        markPreviousAsDone(console);
    }

    private void renderCurrent(Console console) {
        if (message == null) {
            return;
        }

        if (console.isInteractive()) {
            console.print(STR."@|bold [ ]|@ \{message}");
        } else {
            console.print(STR."\{message}...");
        }
    }

    private void markPreviousAsDone(Console console) {
        if (previousMessage == null) {
            return;
        }

        if (console.isInteractive()) {
            console.clearLine();
            console.printLn(STR."@|bold [|@@|bold,green ✓|@@|bold ]|@ \{previousMessage}");
        } else {
            console.printLn(" DONE");
        }
    }
}
