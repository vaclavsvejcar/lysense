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
            console.print("@|bold [ ]|@ %s".formatted(message));
        } else {
            console.print("%s...".formatted(message));
        }
    }

    private void markPreviousAsDone(Console console) {
        if (previousMessage == null) {
            return;
        }

        if (console.isInteractive()) {
            console.clearLine();
            console.printLn("@|bold [|@@|bold,green ✓|@@|bold ]|@ %s".formatted(previousMessage));
        } else {
            console.printLn(" DONE");
        }
    }
}
