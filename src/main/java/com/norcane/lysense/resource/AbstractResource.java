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
package com.norcane.lysense.resource;

import com.google.common.base.MoreObjects;
import com.google.common.io.CharStreams;
import com.norcane.lysense.resource.exception.CannotReadResourceException;
import com.norcane.lysense.resource.exception.ResourceNotWritableException;
import com.norcane.lysense.resource.util.LineSeparator;
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
            throw new IllegalArgumentException(STR."Illegal scheme in URI \{uri}, got '\{uriScheme}', expected '\{scheme}'");
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
