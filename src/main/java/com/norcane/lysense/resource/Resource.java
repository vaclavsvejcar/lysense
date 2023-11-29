package com.norcane.lysense.resource;

import com.norcane.lysense.resource.util.LineSeparator;
import com.norcane.toolkit.ValueClass;

import java.io.Reader;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.norcane.toolkit.Prelude.nonNull;

public interface Resource {

    /**
     * Name of the resource (i.e. file name without extension)
     *
     * @return name of the resource
     */
    String name();

    /**
     * Extension of the resource (i.e. file extension)
     *
     * @return extension of the resource
     */
    String extension();

    /**
     * Location of the resource (i.e. absolute path of file, URL of network resource, etc.)
     *
     * @return location of the resource
     */
    URI uri();

    /**
     * Tries to automatically detect the <i>line separator</i> of the resource. If automatic detection fails, returns system line separator.
     *
     * @return line separator
     */
    LineSeparator lineSeparator();

    /**
     * Returns new reader of the resource.
     *
     * @return reader of the resource
     */
    Reader reader();

    /**
     * Reads the resource content into string. For larger resources always consider using {@link #reader()} instead as this might lead to large memory use.
     *
     * @return resource content as a string
     */
    String readAsString();

    /**
     * Reads the resource as list of lines. For larger resources always consider using {@link #reader()} instead as this might lead to large memory use.
     *
     * @return resource content as list of lines
     */
    List<String> readLines();

    /**
     * Returns this resource as {@link WritableResource} (if supported), otherwise throws
     * {@link com.norcane.lysense.resource.exception.ResourceNotWritableException}.
     *
     * @return this resource as {@link WritableResource}
     */
    WritableResource asWritableOrFail();

    record Scheme(String value) implements ValueClass<String> {

        public static Optional<Scheme> parse(String path) {
            nonNull(path);

            final int index = path.indexOf(':');
            return (index != -1) ? Optional.of(new Scheme(path.substring(0, index))) : Optional.empty();

        }
    }
}
