package com.norcane.lysense.ui.alert;

import com.norcane.lysense.ui.UIComponent;

/**
 * Represents an <i>alert</i> {@link UIComponent} with a title and a message of selected severity.
 */
public interface Alert extends UIComponent {

    /**
     * Creates an {@link Alert} with a given title and message of <i>INFO</i> severity.
     *
     * @param message message to display
     * @return alert
     */
    static Alert info(String message) {
        return console -> console.printLn(alert('i', "blue", "INFO", message));
    }

    /**
     * Creates an {@link Alert} with a given title and message of <i>ERROR</i> severity.
     *
     * @param title   title to display
     * @param message message to display
     * @return alert
     */
    static Alert error(String title, String message) {
        return console -> console.printLn(alert('!', "red", title, message));
    }

    private static String alert(char icon, String color, String title, String message) {
        return STR."@|bold,bg(\{color}),white [\{icon}] \{title}:|@ @|bold,\{color} \{message}|@";
    }
}
