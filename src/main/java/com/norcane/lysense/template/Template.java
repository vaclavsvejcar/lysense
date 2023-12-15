package com.norcane.lysense.template;

import com.norcane.lysense.resource.Resource;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Represents compiled template that can be rendered using provided placeholder variables.
 */
public interface Template {

    /**
     * Resource used to load the template from.
     *
     * @return resource of the template
     */
    Resource resource();

    /**
     * Renders the template using provided placeholder {@link Variables} to given {@link Writer}.
     *
     * @param writer    writer to write rendered template into
     * @param variables values used to replace the placeholders
     * @return given writer for convenience
     */
    Writer render(Writer writer, Variables variables);

    /**
     * Variant of {@link #render(Writer, Variables)} that renders the template using provided placeholder {@link Variables} to {@link String}.
     *
     * @param variables values used to replace the placeholders
     * @return rendered template as string
     */
    default String render(Variables variables) {
        return render(new StringWriter(), variables).toString();
    }
}
