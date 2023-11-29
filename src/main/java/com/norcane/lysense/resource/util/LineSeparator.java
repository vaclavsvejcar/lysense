package com.norcane.lysense.resource.util;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.CannotReadResourceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Represents line separator of textual resource.
 */
public enum LineSeparator {

    /**
     * Classic Mac OS.
     */
    CR("\r"),

    /**
     * MS Windows.
     */
    CRLF("\r\n"),

    /**
     * Unix (OSX, macOS, Linux).
     */
    LF("\n");

    private final String separator;

    LineSeparator(String separator) {
        this.separator = nonNull(separator);
    }

    /**
     * Parses {@link LineSeparator} from given string.
     *
     * @param separator string representation of line separator to parse
     * @return parsed {@link LineSeparator}
     */
    public static Optional<LineSeparator> from(String separator) {
        return Arrays.stream(values())
            .filter(value -> value.separator().equals(separator))
            .findAny();
    }

    public static Optional<LineSeparator> detect(Resource resource) {
        try (final BufferedReader reader = new BufferedReader(resource.reader())) {
            int r;
            while ((r = reader.read()) != -1) {
                final char c = (char) r;

                if (c == '\r') {
                    final int next = reader.read();
                    return Optional.of((next != -1 && ((char) next) == '\n') ? LineSeparator.CRLF : LineSeparator.CR);

                } else if (c == '\n') {
                    return Optional.of(LineSeparator.LF);
                }
            }

            return Optional.empty();
        } catch (IOException e) {
            throw new CannotReadResourceException(resource, e);
        }
    }

    public static LineSeparator platform() {
        final String sep = System.lineSeparator();
        return LineSeparator.from(sep)
            .orElseThrow(() -> new IllegalStateException(STR. "unknown line separator: \{ sep }" ));
    }

    /**
     * Line separator sequence.
     */
    public String separator() {
        return separator;
    }
}
