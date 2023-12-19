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
