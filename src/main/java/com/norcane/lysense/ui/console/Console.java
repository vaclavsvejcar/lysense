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
package com.norcane.lysense.ui.console;

import com.norcane.lysense.ui.UIComponent;

/**
 * Represents a wrapper over system terminal, used to access standard input and output, with few tweaks required by the appplication. Note that any
 * implementation must accept special ANSI color escape-codes, e.g. {@code @|bold,red Warning!|@}.
 *
 * @see <a href="https://fusesource.github.io/jansi/documentation/api/org/fusesource/jansi/AnsiRenderer.html">Jansi AnsiRenderer</a>
 */
public interface Console {

    /**
     * Prints text to console, without ending newline.
     *
     * @param text text to print
     */
    void print(String text);

    /**
     * Prints text to console, with ending newline.
     *
     * @param text text to print
     */
    void printLn(String text);

    /**
     * Clears current line if supported by underlying terminal implementation, otherwise does nothing.
     */
    void clearLine();

    /**
     * Detects whether current terminal implementation is <i>interactive</i>, i.e. supports cursor movement, clearing current line, etc.
     *
     * @return {@code true} if current terminal is interactive
     */
    boolean isInteractive();

    /**
     * Prints empty line to the console.
     */
    default void emptyLine() {
        printLn("");
    }

    /**
     * Renders given {@link UIComponent} to the console.
     *
     * @param component component to render
     */
    default void render(final UIComponent component) {
        component.render(this);
    }
}
