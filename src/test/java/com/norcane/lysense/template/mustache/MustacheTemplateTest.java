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
package com.norcane.lysense.template.mustache;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.template.Template;
import com.norcane.lysense.template.Variables;
import com.norcane.lysense.template.exception.MissingTemplateVariableException;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@QuarkusTest
class MustacheTemplateTest {

    @Test
    public void render() {
        final String expected = "Hello John, 42 years old";
        final String templateName = "my-template";
        final String templateType = "mustache";
        final Resource resource = InlineResource.of(templateName, templateType, "Hello {{name}}, {{info.age}} years old");
        final Template template = MustacheTemplate.compile(resource);

        final Map<String, Object> variables = Map.of(
            "name", "John",
            "info", new UserInfo(42)
        );

        assertEquals(resource, template.resource());
        assertEquals(expected, template.render(new StringWriter(), Variables.from(variables)).toString());
    }

    @Test
    void render_ioException() throws IOException {
        final Resource resource = InlineResource.of("test");
        final Template template = MustacheTemplate.compile(resource);

        // -- mocks
        final Writer writer = mock(Writer.class);
        doThrow(IOException.class).when(writer).flush();

        assertThrows(UncheckedIOException.class, () -> template.render(writer, Variables.empty()));

        // -- verify
        verify(writer).flush();
    }

    @Test
    void render_missingVariable() {
        final Resource resource = InlineResource.of("Hello, {{name}}!");
        final Template template = MustacheTemplate.compile(resource);

        assertThrows(MissingTemplateVariableException.class, () -> template.render(new StringWriter(), Variables.empty()));
    }

    @Test
    void testToString() {
        final Resource resource = InlineResource.of("Hello {{name}}, {{info.age}} years old");
        final Template template = MustacheTemplate.compile(resource);

        assertNotNull(template.toString());
    }

    private record UserInfo(int age) {
    }
}
