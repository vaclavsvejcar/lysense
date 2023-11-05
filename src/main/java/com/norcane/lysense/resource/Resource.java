package com.norcane.lysense.resource;

import com.norcane.toolkit.ValueClass;

import java.io.Reader;
import java.net.URI;
import java.util.Optional;

public interface Resource {

    String name();

    String extension();

    URI uri();

    LineSeparator lineSeparator();

    Reader reader();

    /**
     * Reads the resource content into string. For larger resources always consider using {@link #reader()} instead as this might lead to large memory use.
     *
     * @return resource content as a string
     */
    String readAsString();

    record Scheme(String value) implements ValueClass<String> {

        public static Optional<Scheme> parse(String path) {
            final int index = path.indexOf(':');
            return (index != -1) ? Optional.of(new Scheme(path.substring(0, index))) : Optional.empty();

        }
    }
}
