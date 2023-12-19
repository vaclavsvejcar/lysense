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
package com.norcane.lysense.resource.classpath;

import com.google.common.base.MoreObjects;
import com.google.common.io.Files;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.toolkit.net.URIs;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import static com.norcane.toolkit.Prelude.nonNull;

public class ClassPathResource extends AbstractResource {

    static final Resource.Scheme SCHEME = new Resource.Scheme("classpath");

    private ClassPathResource(URI uri) {
        super(Files.getNameWithoutExtension(uri.getSchemeSpecificPart()), Files.getFileExtension(uri.getSchemeSpecificPart()), uri);
    }

    public static ClassPathResource of(URI uri) {
        nonNull(uri);
        enforceScheme(uri, SCHEME);

        if (ClassPathResource.class.getResource(uri.getSchemeSpecificPart()) == null) {
            throw new ResourceNotFoundException(uri);
        }

        return new ClassPathResource(uri);
    }

    public static ClassPathResource of(String classPath) {
        return of(URIs.create(STR."\{SCHEME.value()}:\{classPath}"));
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(nonNull(getClass().getResourceAsStream(location.getSchemeSpecificPart())));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("location", location)
            .toString();
    }
}
