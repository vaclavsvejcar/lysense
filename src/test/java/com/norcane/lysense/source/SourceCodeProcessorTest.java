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
package com.norcane.lysense.source;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.HeaderStyle;
import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.template.TemplateManager;
import com.norcane.lysense.template.Variables;
import com.norcane.lysense.template.mustache.MustacheTemplate;
import com.norcane.lysense.template.source.UserLicenseTemplateSource;
import com.norcane.lysense.test.InMemoryWritableResourceWrapper;
import com.norcane.lysense.test.TestHeaderConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static com.norcane.lysense.domain.LanguageId.languageId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@QuarkusTest
class SourceCodeProcessorTest {

    @Inject
    SourceCodeProcessor sourceCodeProcessor;

    @InjectMock
    Configuration configuration;

    @InjectMock
    TemplateManager templateManager;

    @BeforeEach
    void beforeEach() {
        sourceCodeProcessor.resetState();
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(configuration, templateManager);
    }

    @Test
    void supportedLanguageIds() {
        assertFalse(sourceCodeProcessor.supportedLanguageIds().isEmpty());
    }

    @Test
    void addHeader_sourceModified() {
        final Variables variables = Variables.from(Map.of("name", "John Smith"));
        final UserLicenseTemplateSource.TemplateKey templateKey = new UserLicenseTemplateSource.TemplateKey("java");
        final InMemoryWritableResourceWrapper resource = new InMemoryWritableResourceWrapper(InlineResource.of(
            "test", "java",
            """
                one
                two
                three
                """
        ));
        final String expected =
            """
                this is template from John Smith
                one
                two
                three
                """;

        // -- mocks
        when(configuration.headerConfigOrFail(languageId("java"))).thenReturn(new TestHeaderConfig(HeaderStyle.BLOCK_COMMENT));
        when(configuration.templateVariables()).thenReturn(variables);
        when(templateManager.template(templateKey))
            .thenReturn(MustacheTemplate.compile(InlineResource.of("java", "mustache", "this is template from John Smith")));

        final SourceModificationResult result = sourceCodeProcessor.addHeader(sourceCodeProcessor.process(resource));
        assertEquals(SourceModificationResult.MODIFIED, result);
        assertEquals(expected, resource.writtenString());

        // -- verify
        verify(configuration, times(2)).headerConfigOrFail(languageId("java"));
        verify(configuration).templateVariables();
        verify(templateManager).template(templateKey);
    }

    @Test
    void addHeader_sourceNotModified() {
        final InMemoryWritableResourceWrapper resource = new InMemoryWritableResourceWrapper(InlineResource.of(
            "test", "java",
            """
                /* this is header */
                one
                two
                three
                """
        ));

        // -- mocks
        when(configuration.headerConfigOrFail(languageId("java"))).thenReturn(new TestHeaderConfig(HeaderStyle.BLOCK_COMMENT));

        final SourceModificationResult result = sourceCodeProcessor.addHeader(sourceCodeProcessor.process(resource));
        assertEquals(SourceModificationResult.NOT_MODIFIED, result);

        // -- verify
        verify(configuration).headerConfigOrFail(languageId("java"));
    }

    @Test
    void dropHeader_sourceModified() {
        final InMemoryWritableResourceWrapper resource = new InMemoryWritableResourceWrapper(InlineResource.of(
            "test", "java",
            """
                one
                two
                                
                /*
                 * this is template from John Smith
                 */
                                
                three
                """
        ));
        final String expected =
            """
                one
                two
                three
                """;

        // -- mocks
        when(configuration.headerConfigOrFail(languageId("java"))).thenReturn(new TestHeaderConfig(HeaderStyle.BLOCK_COMMENT));

        final SourceModificationResult result = sourceCodeProcessor.dropHeader(sourceCodeProcessor.process(resource));
        assertEquals(SourceModificationResult.MODIFIED, result);
        assertEquals(expected, resource.writtenString());

        // -- verify
        verify(configuration).headerConfigOrFail(languageId("java"));
    }

    @Test
    void dropHeader_sourceNotModified() {
        final InMemoryWritableResourceWrapper resource = new InMemoryWritableResourceWrapper(InlineResource.of(
            "test", "java",
            """
                one
                two
                three
                """
        ));

        // -- mocks
        when(configuration.headerConfigOrFail(languageId("java"))).thenReturn(new TestHeaderConfig(HeaderStyle.BLOCK_COMMENT));
        final SourceModificationResult result = sourceCodeProcessor.dropHeader(sourceCodeProcessor.process(resource));
        assertEquals(SourceModificationResult.NOT_MODIFIED, result);

        // -- verify
        verify(configuration).headerConfigOrFail(languageId("java"));
    }

    @Test
    void updateHeader_sourceModified() {
        final Variables variables = Variables.from(Map.of("name", "John Smith"));
        final UserLicenseTemplateSource.TemplateKey templateKey = new UserLicenseTemplateSource.TemplateKey("java");
        final InMemoryWritableResourceWrapper resource = new InMemoryWritableResourceWrapper(InlineResource.of(
            "test", "java",
            """
                one
                two
                                
                /*
                 * this is template from John Smith
                 */
                                
                three
                """
        ));
        final String newTemplate =
            """
                /*
                 * this is
                 * new template from John Smith
                 */
                 """;
        final String expected =
            """
                one
                two
                /*
                 * this is
                 * new template from John Smith
                 */
                three
                """;

        // -- mocks
        when(configuration.headerConfigOrFail(languageId("java"))).thenReturn(new TestHeaderConfig(HeaderStyle.BLOCK_COMMENT));
        when(configuration.templateVariables()).thenReturn(variables);
        when(templateManager.template(templateKey))
            .thenReturn(MustacheTemplate.compile(InlineResource.of("java", "mustache", newTemplate.trim())));

        final SourceModificationResult result = sourceCodeProcessor.updateHeader(sourceCodeProcessor.process(resource));
        assertEquals(SourceModificationResult.MODIFIED, result);
        assertEquals(expected, resource.writtenString());

        // -- verify
        verify(configuration, times(2)).headerConfigOrFail(languageId("java"));
        verify(configuration).templateVariables();
        verify(templateManager).template(templateKey);
    }

    @Test
    void updateHeader_sourceNotModified() {
        final Variables variables = Variables.from(Map.of("name", "John Smith"));
        final UserLicenseTemplateSource.TemplateKey templateKey = new UserLicenseTemplateSource.TemplateKey("java");
        final InMemoryWritableResourceWrapper resource = new InMemoryWritableResourceWrapper(InlineResource.of(
            "test", "java",
            """
                one
                two
                /*
                 * this is template from John Smith
                 */
                three
                """
        ));
        final String newTemplate =
            """
                /*
                 * this is template from John Smith
                 */
                 """;

        // -- mocks
        when(configuration.headerConfigOrFail(languageId("java"))).thenReturn(new TestHeaderConfig(HeaderStyle.BLOCK_COMMENT));
        when(configuration.templateVariables()).thenReturn(variables);
        when(templateManager.template(templateKey))
            .thenReturn(MustacheTemplate.compile(InlineResource.of("java", "mustache", newTemplate.trim())));

        final SourceModificationResult result = sourceCodeProcessor.updateHeader(sourceCodeProcessor.process(resource));
        assertEquals(SourceModificationResult.NOT_MODIFIED, result);

        // -- verify
        verify(configuration, times(2)).headerConfigOrFail(languageId("java"));
        verify(configuration).templateVariables();
        verify(templateManager).template(templateKey);
    }
}
