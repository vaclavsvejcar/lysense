package com.norcane.lysense.test;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.WritableResource;
import com.norcane.lysense.resource.util.LineSeparator;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Wrapper for {@link Resource} that allows to capture the written content into memory for testing purposes.
 */
public class InMemoryWritableResourceWrapper implements WritableResource {

    private final Resource resource;
    private final StringWriter writer;

    public InMemoryWritableResourceWrapper(Resource resource) {
        this.resource = nonNull(resource);
        this.writer = new StringWriter();
    }

    public String writtenString() {
        return writer.toString();
    }

    @Override
    public String name() {
        return resource.name();
    }

    @Override
    public String extension() {
        return resource.extension();
    }

    @Override
    public URI uri() {
        return resource.uri();
    }

    @Override
    public Reader reader() {
        return resource.reader();
    }

    @Override
    public Writer writer() {
        return writer;
    }

    @Override
    public LineSeparator lineSeparator() {
        return resource.lineSeparator();
    }

    @Override
    public WritableResource asWritableOrFail() {
        return this;
    }

    @Override
    public String readAsString() {
        return resource.readAsString();
    }

    @Override
    public List<String> readLines() {
        return resource.readLines();
    }
}
