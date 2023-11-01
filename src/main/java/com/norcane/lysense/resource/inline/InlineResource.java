package com.norcane.lysense.resource.inline;


import com.google.common.net.UrlEscapers;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;

import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Base64;

import static com.norcane.toolkit.Prelude.nonNull;

public class InlineResource extends AbstractResource {

    static final String DEFAULT_NAME = "inline-content";
    static final String DEFAULT_EXTENSION = "txt";
    static final Resource.Scheme SCHEME = new Resource.Scheme("inline");

    private final String content;

    protected InlineResource(String name, String type, String content, URI location) {
        super(name, type, location);

        this.content = content;
    }

    public static InlineResource of(URI uri) {
        nonNull(uri);
        enforceSchemeIfPresent(uri, SCHEME);

        final String[] chunks = uri.getSchemeSpecificPart().split(";");
        final String type = chunks[0];
        final String name = chunks[1].split("=")[1];
        final String content = new String(Base64.getDecoder().decode(chunks[2].split(",")[1].getBytes()));

        return new InlineResource(name, type, content, uri);
    }

    public static InlineResource of(String content) {
        return of(DEFAULT_NAME, DEFAULT_EXTENSION, nonNull(content));
    }

    public static InlineResource of(String name, String extension, String content) {
        nonNull(name);
        nonNull(extension);
        nonNull(content);

        final String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
        final String encodedName = UrlEscapers.urlFragmentEscaper().escape(name);
        final URI uri = URI.create(STR. "\{ SCHEME.value() }:\{ extension };name=\{ encodedName };base64,\{ encodedContent }" );
        return new InlineResource(name, extension, content, uri);
    }

    @Override
    public Reader reader() {
        return new StringReader(content);
    }

    @Override
    public String readAsString() {
        // specialized implementation, to avoid calling #reader() and creating new Reader instance
        return content;
    }
}
