package com.norcane.lysense.ui.console;


import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@ApplicationScoped
public class NoopConsole implements Console {

    @Override
    public void print(String text) {
        // do nothing
    }

    @Override
    public void printLn(String text) {
        // do nothing
    }

    @Override
    public void clearLine() {
        // do nothing
    }

    @Override
    public boolean isInteractive() {
        return false;
    }
}
