package com.norcane.lysense.resource;

import com.google.common.io.CharStreams;

import com.norcane.lysense.resource.exception.CannotReadResourceException;
import com.norcane.toolkit.state.Memoized;

import java.io.Reader;
import java.net.URI;

public abstract class AbstractResource implements Resource {

    protected final String name;
    protected final String extension;
    protected final URI location;

    private final Memoized<LineSeparator> lineSeparator = Memoized.empty();

    protected AbstractResource(String name, String extension, URI location) {
        this.name = name;
        this.extension = extension;
        this.location = location;
    }


    protected static void enforceSchemeIfPresent(URI uri, Resource.Scheme scheme) {
        final String uriScheme = uri.getScheme();
        if (uriScheme != null && !uriScheme.equals(scheme.value())) {
            throw new IllegalArgumentException(STR. "Illegal resource URI scheme, got '\{ uriScheme }', expected '\{ scheme.value() }'" );
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
    public URI location() {
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
}
