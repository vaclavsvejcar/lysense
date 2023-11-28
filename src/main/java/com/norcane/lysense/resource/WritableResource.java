package com.norcane.lysense.resource;

import java.io.Writer;

/**
 * Represents a {@link Resource} that can be written to.
 *
 * @see Resource
 */
public interface WritableResource extends Resource {

    /**
     * Returns new {@link Writer} for the resource.
     */
    Writer writer();
}
