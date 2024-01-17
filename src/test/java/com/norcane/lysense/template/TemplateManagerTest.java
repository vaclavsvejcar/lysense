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
package com.norcane.lysense.template;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.template.source.TemplateSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class TemplateManagerTest {

    @Inject
    TemplateManager templateManager;

    @BeforeEach
    void beforeEach() {
        templateManager.resetState();
    }

    @Test
    void template() {
        final TestTemplateSource.TemplateKey templateKey1 = new TestTemplateSource.TemplateKey("test-template1");
        final TestTemplateSource.TemplateKey templateKey2 = new TestTemplateSource.TemplateKey("test-template2");

        final Template template1 = templateManager.template(templateKey1);
        assertEquals("test-template1", template1.resource().name());
        assertEquals("mustache", template1.resource().extension());
        assertEquals("Hello, John!", template1.render(Variables.from(Map.of("name", "John"))));

        final Template template2 = templateManager.template(templateKey2);
        assertEquals("test-template2", template2.resource().name());
        assertEquals("mustache", template2.resource().extension());
        assertEquals("Goodbye, John!", template2.render(Variables.from(Map.of("name", "John"))));
    }

    @Test
    void templates() {
        final Map<TestTemplateSource.TemplateKey, Template> templates = templateManager.templates(TestTemplateSource.TemplateKey.class);

        assertEquals(2, templates.size());
        assertTrue(templates.containsKey(new TestTemplateSource.TemplateKey("test-template1")));
        assertTrue(templates.containsKey(new TestTemplateSource.TemplateKey("test-template2")));
    }

    @Test
    void loadedTemplateSources() {
        // none template source has been dynamically loaded yet
        assertTrue(templateManager.loadedTemplateSources().isEmpty());

        // force loading of the 'TestTemplateSource' template source
        templateManager.template(new TestTemplateSource.TemplateKey("test-template1"));

        // check that the 'TestTemplateSource' template source has been loaded
        assertTrue(templateManager.loadedTemplateSources().contains(TestTemplateSource.TemplateKey.class));

    }

    @ApplicationScoped
    public static class TestTemplateSource extends TemplateSource<TestTemplateSource.TemplateKey> {

        @Override
        public Class<TemplateKey> templateKeyClass() {
            return TestTemplateSource.TemplateKey.class;
        }

        @Override
        protected TemplateKey templateKey(Resource resource) {
            return new TemplateKey(resource.name());
        }

        @Override
        protected List<Resource> resources() {
            return List.of(
                InlineResource.of("test-template1", "mustache", "Hello, {{name}}!"),
                InlineResource.of("test-template2", "mustache", "Goodbye, {{name}}!")
            );
        }

        public record TemplateKey(String name) implements com.norcane.lysense.template.TemplateKey {
        }
    }
}
