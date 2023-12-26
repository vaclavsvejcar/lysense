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
package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.WritableResource;
import com.norcane.lysense.resource.exception.CannotReadResourceException;
import com.norcane.lysense.resource.exception.CannotWriteResourceException;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemResource extends AbstractResource implements WritableResource {

    public static final Resource.Scheme SCHEME = new Resource.Scheme("file");

    private final Path path;

    private FileSystemResource(Path path, URI uri) {
        super(com.google.common.io.Files.getNameWithoutExtension(path.toString()),
              com.google.common.io.Files.getFileExtension(path.toString()),
              path.getParent() != null ? path.getParent().toString() : null,
              uri);

        this.path = path;
    }

    public static FileSystemResource of(URI uri) {
        final Path path = Path.of(uri.getSchemeSpecificPart());

        if (!Files.isRegularFile(path)) {
            throw new ResourceNotFoundException(uri);
        }

        return new FileSystemResource(path, uri);
    }

    public static FileSystemResource of(String path) {
        return of(Path.of(path).toUri());
    }

    public static FileSystemResource of(Path path) {
        return of(path.toUri());
    }

    @Override
    public Reader reader() {
        try {
            return Files.newBufferedReader(path);
        } catch (IOException e) {
            throw new CannotReadResourceException(this, e);
        }
    }

    @Override
    public Writer writer() {
        try {
            return Files.newBufferedWriter(path);
        } catch (Exception e) {
            throw new CannotWriteResourceException(this, e);
        }
    }
}
