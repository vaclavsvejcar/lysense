/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.norcane.lysense.resource.inline;


import com.google.common.io.Files;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;
import com.norcane.toolkit.net.URIs;

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

    protected InlineResource(String path, String extension, String parent, String content, URI location) {
        super(path, extension, parent, location);

        this.content = content;
    }

    public static InlineResource of(URI uri) {
        nonNull(uri);
        enforceScheme(uri, SCHEME);

        final String[] chunks = uri.getSchemeSpecificPart().split(";");
        final String extension = chunks[0];
        final String path = chunks[1].split("=")[1];
        final String name = Files.getNameWithoutExtension(path);
        final String parent = parent(path);
        final String content = new String(Base64.getDecoder().decode(chunks[2].split(",")[1].getBytes()));

        return new InlineResource(name, extension, parent, content, uri);
    }

    public static InlineResource of(String content) {
        return of(DEFAULT_NAME, DEFAULT_EXTENSION, nonNull(content));
    }

    public static InlineResource of(String path, String extension, String content) {
        nonNull(path);
        nonNull(extension);
        nonNull(content);

        final String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
        final String name = Files.getNameWithoutExtension(path);
        final String encodedName = URIs.escape(path);
        final String parent = parent(path);
        final URI uri = URIs.create(STR."\{SCHEME.value()}:\{extension};name=\{encodedName};base64,\{encodedContent}");
        return new InlineResource(name, extension, parent, content, uri);
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
