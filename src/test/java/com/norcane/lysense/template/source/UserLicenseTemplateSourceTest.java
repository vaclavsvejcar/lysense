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
package com.norcane.lysense.template.source;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.template.exception.DuplicateTemplatesFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@QuarkusTest
class UserLicenseTemplateSourceTest {

    @InjectMock
    Configuration configuration;

    @InjectMock
    ResourceLoader resourceManager;

    @Inject
    UserLicenseTemplateSource source;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(configuration, resourceManager);
    }

    @Test
    void templateKeyClass() {
        assertEquals(UserLicenseTemplateSource.TemplateKey.class, source.templateKeyClass());
    }

    @Test
    void templateKey() {
        final Resource resource = InlineResource.of("java", "mustache", "template");
        assertEquals(new UserLicenseTemplateSource.TemplateKey("java"), source.templateKey(resource));
    }

    @Test
    void resources() {
        final String templatesPath = "templates";
        final Resource template1 = InlineResource.of("test", "mustache", "Hello, {{name}}!");
        final Resource template2 = InlineResource.of("test", "freemarker", "Hello, ${name}!");

        // -- mocks
        when(configuration.templates()).thenReturn(List.of(templatesPath));
        when(resourceManager.resources(eq(templatesPath), any(), eq(true)))
            .thenReturn(List.of(template1))             // valid state - only one template type for source type
            .thenReturn(List.of(template1, template2)); // invalid state - two possible templates for one source type

        final Map<UserLicenseTemplateSource.TemplateKey, Resource> templates = source.templateResources();
        assertTrue(templates.containsKey(new UserLicenseTemplateSource.TemplateKey("test")));

        // check that map of loaded templates cannot be modified
        assertThrows(UnsupportedOperationException.class, () -> templates.put(new UserLicenseTemplateSource.TemplateKey("uh"), null));

        // check that exception is thrown when two templates of same name are found for one source type
        assertThrows(DuplicateTemplatesFoundException.class, () -> source.templateResources());

        // -- verify
        verify(configuration, times(2)).templates();
        verify(resourceManager, times(2)).resources(eq(templatesPath), any(), eq(true));
    }
}
