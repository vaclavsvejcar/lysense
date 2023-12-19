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
package com.norcane.lysense.configuration.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializer for enums that are represented as lower-case dash-separated strings.
 *
 * @param <T> enum type
 */
public class LowerCaseDashSeparatedEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
    private final Class<T> enumClass;

    private LowerCaseDashSeparatedEnumDeserializer(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * Constructs a new instance of {@link LowerCaseDashSeparatedEnumDeserializer} for the given enum class.
     *
     * @param enumClass enum class
     * @param <T>       enum type
     * @return new instance of {@link LowerCaseDashSeparatedEnumDeserializer}
     */
    public static <T extends Enum<T>> LowerCaseDashSeparatedEnumDeserializer<T> forEnum(Class<T> enumClass) {
        return new LowerCaseDashSeparatedEnumDeserializer<>(enumClass);
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {

        final String value = jsonParser.getText();
        final String upperCaseValue = value.replace('-', '_').toUpperCase();

        return Enum.valueOf(enumClass, upperCaseValue);
    }
}
