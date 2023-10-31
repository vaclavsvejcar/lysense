package com.norcane.lysense.resource.inline;


import com.google.common.net.UrlEscapers;

import com.norcane.lysense.resource.AbstractResource;

import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Base64;

public class InlineResource extends AbstractResource {

    static final String DEFAULT_NAME = "inline-content";
    static final String DEFAULT_EXTENSION = "txt";
    static final String URI_SCHEME = "inline";

    private final String content;

    protected InlineResource(String name, String type, String content, URI location) {
        super(name, type, location);

        this.content = content;
    }

    public static InlineResource of(URI uri) throws IllegalArgumentException {
        if (!uri.getScheme().equals(URI_SCHEME)) {
            throw new IllegalArgumentException(STR. "Illegal inline resource URI '\{ uri }', expected scheme '\{ URI_SCHEME }'" );
        }

        final String[] chunks = uri.getSchemeSpecificPart().split(";");
        final String type = chunks[0];
        final String name = chunks[1].split("=")[1];
        final String content = new String(Base64.getDecoder().decode(chunks[2].split(",")[1].getBytes()));

        return new InlineResource(name, type, content, uri);
    }

    public static InlineResource of(String content) {
        return of(DEFAULT_NAME, DEFAULT_EXTENSION, content);
    }

    public static InlineResource of(String name, String extension, String content) {
        final String contentBase64 = Base64.getEncoder().encodeToString(content.getBytes());
        final String nameEncoded = UrlEscapers.urlFragmentEscaper().escape(name);
        final URI uri = URI.create(STR. "\{ URI_SCHEME }:\{ extension };name=\{ nameEncoded };base64,\{ contentBase64 }" );
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
