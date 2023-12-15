package com.norcane.lysense.source;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.HeaderStyle;
import com.norcane.lysense.resource.inline.InlineResource;
import com.norcane.lysense.template.Template;
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

import static com.norcane.lysense.source.LanguageId.languageId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@QuarkusTest
class SourceCodeProcessorTest {

    static final Template TEST_TEMPLATE = MustacheTemplate.compile(InlineResource.of("java", "mustache", "test"));

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
        when(templateManager.templates(UserLicenseTemplateSource.TemplateKey.class)).thenReturn(Map.of(
            templateKey, TEST_TEMPLATE
        ));
        when(templateManager.template(templateKey))
            .thenReturn(MustacheTemplate.compile(InlineResource.of("java", "mustache", "this is template from John Smith")));

        final SourceModificationResult result = sourceCodeProcessor.addHeader(sourceCodeProcessor.load(resource));
        assertEquals(SourceModificationResult.MODIFIED, result);
        assertEquals(expected, resource.writtenString());

        // -- verify
        verify(configuration, times(2)).headerConfigOrFail(languageId("java"));
        verify(configuration).templateVariables();
        verify(templateManager).templates(UserLicenseTemplateSource.TemplateKey.class);
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
        when(templateManager.templates(UserLicenseTemplateSource.TemplateKey.class)).thenReturn(Map.of(
            new UserLicenseTemplateSource.TemplateKey("java"), TEST_TEMPLATE
        ));

        final SourceModificationResult result = sourceCodeProcessor.addHeader(sourceCodeProcessor.load(resource));
        assertEquals(SourceModificationResult.NOT_MODIFIED, result);

        // -- verify
        verify(configuration).headerConfigOrFail(languageId("java"));
        verify(templateManager).templates(UserLicenseTemplateSource.TemplateKey.class);
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
        when(templateManager.templates(UserLicenseTemplateSource.TemplateKey.class)).thenReturn(Map.of(
            new UserLicenseTemplateSource.TemplateKey("java"), TEST_TEMPLATE
        ));

        final SourceModificationResult result = sourceCodeProcessor.dropHeader(sourceCodeProcessor.load(resource));
        assertEquals(SourceModificationResult.MODIFIED, result);
        assertEquals(expected, resource.writtenString());

        // -- verify
        verify(configuration).headerConfigOrFail(languageId("java"));
        verify(templateManager).templates(UserLicenseTemplateSource.TemplateKey.class);
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
        when(templateManager.templates(UserLicenseTemplateSource.TemplateKey.class)).thenReturn(Map.of(
            new UserLicenseTemplateSource.TemplateKey("java"), TEST_TEMPLATE
        ));

        final SourceModificationResult result = sourceCodeProcessor.dropHeader(sourceCodeProcessor.load(resource));
        assertEquals(SourceModificationResult.NOT_MODIFIED, result);

        // -- verify
        verify(configuration).headerConfigOrFail(languageId("java"));
        verify(templateManager).templates(UserLicenseTemplateSource.TemplateKey.class);
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
        when(templateManager.templates(UserLicenseTemplateSource.TemplateKey.class)).thenReturn(Map.of(
            templateKey, TEST_TEMPLATE
        ));
        when(templateManager.template(templateKey))
            .thenReturn(MustacheTemplate.compile(InlineResource.of("java", "mustache", newTemplate.trim())));

        final SourceModificationResult result = sourceCodeProcessor.updateHeader(sourceCodeProcessor.load(resource));
        assertEquals(SourceModificationResult.MODIFIED, result);
        assertEquals(expected, resource.writtenString());

        // -- verify
        verify(configuration, times(2)).headerConfigOrFail(languageId("java"));
        verify(configuration).templateVariables();
        verify(templateManager).templates(UserLicenseTemplateSource.TemplateKey.class);
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
        when(templateManager.templates(UserLicenseTemplateSource.TemplateKey.class)).thenReturn(Map.of(
            templateKey, TEST_TEMPLATE
        ));
        when(templateManager.template(templateKey))
            .thenReturn(MustacheTemplate.compile(InlineResource.of("java", "mustache", newTemplate.trim())));

        final SourceModificationResult result = sourceCodeProcessor.updateHeader(sourceCodeProcessor.load(resource));
        assertEquals(SourceModificationResult.NOT_MODIFIED, result);

        // -- verify
        verify(configuration, times(2)).headerConfigOrFail(languageId("java"));
        verify(configuration).templateVariables();
        verify(templateManager).templates(UserLicenseTemplateSource.TemplateKey.class);
        verify(templateManager).template(templateKey);
    }
}