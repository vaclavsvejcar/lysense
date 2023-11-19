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