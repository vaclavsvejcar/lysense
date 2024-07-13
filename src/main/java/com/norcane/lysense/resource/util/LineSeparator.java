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
package com.norcane.lysense.resource.util;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.CannotReadResourceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Represents line separator of textual resource.
 */
public enum LineSeparator {

    /**
     * Classic Mac OS.
     */
    CR("\r"),

    /**
     * MS Windows.
     */
    CRLF("\r\n"),

    /**
     * Unix (OSX, macOS, Linux).
     */
    LF("\n");

    private final String separator;

    LineSeparator(String separator) {
        this.separator = nonNull(separator);
    }

    /**
     * Parses {@link LineSeparator} from given string.
     *
     * @param separator string representation of line separator to parse
     * @return parsed {@link LineSeparator}
     */
    public static Optional<LineSeparator> from(String separator) {
        return Arrays.stream(values())
            .filter(value -> value.separator().equals(separator))
            .findAny();
    }

    public static Optional<LineSeparator> detect(Resource resource) {
        try (final BufferedReader reader = new BufferedReader(resource.reader())) {
            int r;
            while ((r = reader.read()) != -1) {
                final char c = (char) r;

                if (c == '\r') {
                    final int next = reader.read();
                    return Optional.of((next != -1 && ((char) next) == '\n') ? LineSeparator.CRLF : LineSeparator.CR);

                } else if (c == '\n') {
                    return Optional.of(LineSeparator.LF);
                }
            }

            return Optional.empty();
        } catch (IOException e) {
            throw new CannotReadResourceException(resource, e);
        }
    }

    public static LineSeparator platform() {
        final String sep = System.lineSeparator();
        return LineSeparator.from(sep)
            .orElseThrow(() -> new IllegalStateException("unknown line separator: " + sep));
    }

    /**
     * Line separator sequence.
     */
    public String separator() {
        return separator;
    }
}
