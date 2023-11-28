package com.norcane.lysense.resource;

import com.google.common.base.MoreObjects;
import com.google.common.io.CharStreams;

import com.norcane.lysense.resource.exception.CannotReadResourceException;
import com.norcane.lysense.resource.exception.ResourceNotWritableException;
import com.norcane.toolkit.state.Memoized;

import java.io.Reader;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Abstract implementation of {@link Resource} interface that implements common functionality such as <i>line separator</i> detection, <i>equals</i> and
 * <i>hashCode</i>, etc.
 */
public abstract class AbstractResource implements Resource {

    protected final String name;
    protected final String extension;
    protected final URI location;

    private final Memoized<LineSeparator> lineSeparator = Memoized.detached();

    protected AbstractResource(String name, String extension, URI location) {
        this.name = name;
        this.extension = extension;
        this.location = location;
    }

    protected static void enforceScheme(URI uri, Resource.Scheme scheme) {
        final String uriScheme = uri.getScheme();
        if (uriScheme == null || !uriScheme.equals(scheme.value())) {
            throw new IllegalArgumentException(STR. "Illegal scheme in URI \{ uri }, got '\{ uriScheme }', expected '\{ scheme.value() }'" );
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String extension() {
        return extension;
    }

    @Override
    public URI uri() {
        return location;
    }

    @Override
    public LineSeparator lineSeparator() {
        return lineSeparator.computeIfAbsent(() -> LineSeparator.detect(this).orElseGet(LineSeparator::platform));
    }

    @Override
    public String readAsString() {
        try (final Reader stream = reader()) {
            return CharStreams.toString(stream);
        } catch (Exception e) {
            throw new CannotReadResourceException(this, e);
        }
    }

    @Override
    public List<String> readLines() {
        return readAsString().lines().toList();
    }

    @Override
    public WritableResource asWritableOrFail() {
        if (this instanceof WritableResource writable) {
            return writable;
        } else {
            throw new ResourceNotWritableException(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractResource that = (AbstractResource) o;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("uri", location)
            .toString();
    }
}
