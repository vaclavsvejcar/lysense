package com.norcane.lysense.ui;

import com.norcane.lysense.ui.console.Console;

/**
 * Represents <i>UI</i> component, that can be rendered as text to the console.
 */
public interface UIComponent {

    /**
     * Render component as a text to the given {@link Console}.
     *
     * @param console console used for rendering
     */
    void render(Console console);
}
