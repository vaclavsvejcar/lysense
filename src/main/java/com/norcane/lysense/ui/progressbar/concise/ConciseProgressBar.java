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
        final String currentFormat = "%" + String.valueOf(maximum).length() + "s";
        final boolean interactive = console.isInteractive();
        final String currentSpinner = interactive ? "@|bold,magenta %s|@ ".formatted(spinner) : "";

        final String rendered = "%s@|bold [%s of %d]|@ %s".formatted(currentSpinner, currentFormat.formatted(current), maximum, message);

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
