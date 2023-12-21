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

import com.norcane.toolkit.state.Memoized;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.LaunchMode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import picocli.CommandLine;

import java.io.IOException;

/**
 * Implementation of {@link Console} using <a href="https://jline.github.io">jLine</a> as the terminal backend library.
 */
@ApplicationScoped
@Unremovable
public class JLineConsole implements Console {

    private final Memoized<Boolean> interactive = Memoized.detached();
    private Terminal terminal;

    @PostConstruct
    void postConstruct() throws IOException {
        terminal = TerminalBuilder.terminal();
    }

    @PreDestroy
    void preDestroy() throws IOException {
        terminal.close();
    }

    @Override
    public void print(String text) {
        System.out.print(ansiString(text));
    }

    @Override
    public void printLn(String text) {
        System.out.println(ansiString(text));
    }

    @Override
    public void clearLine() {
        if (isInteractive()) {
            print("\u001b[1000D");
            print("\u001b[0K");
        }
    }

    @Override
    public boolean isInteractive() {
        return interactive.computeIfAbsent(
                () -> LaunchMode.current() == LaunchMode.NORMAL &&
                        terminal.getStringCapability(InfoCmp.Capability.cursor_up) != null &&
                        terminal.getStringCapability(InfoCmp.Capability.cursor_down) != null);
    }

    private String ansiString(final String text) {
        return CommandLine.Help.Ansi.AUTO.string(text);
    }
}
