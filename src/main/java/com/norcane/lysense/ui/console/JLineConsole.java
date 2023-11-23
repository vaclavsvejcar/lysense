package com.norcane.lysense.ui.console;

import com.norcane.toolkit.state.Memoized;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.LaunchMode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

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
