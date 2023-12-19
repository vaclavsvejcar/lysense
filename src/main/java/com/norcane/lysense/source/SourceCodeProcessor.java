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
import com.norcane.lysense.configuration.api.HeaderConfig;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.util.LineSeparator;
import com.norcane.lysense.source.metadata.HeaderCandidate;
import com.norcane.lysense.source.metadata.LicenseHeader;
import com.norcane.lysense.source.support.SourceCodeSupport;
import com.norcane.lysense.splicer.Operation;
import com.norcane.lysense.splicer.ResourceSplicer;
import com.norcane.lysense.template.Template;
import com.norcane.lysense.template.TemplateManager;
import com.norcane.lysense.template.Variables;
import com.norcane.lysense.template.source.UserLicenseTemplateSource;
import com.norcane.toolkit.state.Memoized;
import com.norcane.toolkit.state.Stateful;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class SourceCodeProcessor implements Stateful {

    private final Configuration configuration;
    private final Instance<SourceCodeSupport> sourceCodeSupports;
    private final ResourceSplicer resourceSplicer;
    private final TemplateManager templateManager;

    private final Memoized<Map<String, SourceCodeSupport>> languageIdToSupport = Memoized.bindTo(this);


    @Inject
    public SourceCodeProcessor(Configuration configuration,
                               Instance<SourceCodeSupport> sourceCodeSupports,
                               ResourceSplicer resourceSplicer,
                               TemplateManager templateManager) {

        this.configuration = configuration;
        this.sourceCodeSupports = sourceCodeSupports;
        this.resourceSplicer = resourceSplicer;
        this.templateManager = templateManager;
    }

    /**
     * Returns set of all known source code language IDs.
     *
     * @return set of source code language IDs
     * @see SourceCodeSupport#languageId()
     */
    public Set<LanguageId> supportedLanguageIds() {
        return sourceCodeSupports.stream()
            .map(SourceCodeSupport::languageId)
            .collect(Collectors.toSet());
    }

    /**
     * Processes and analyzes {@link SourceCode}.
     *
     * @param resource resource to process source code from
     * @return processed source code
     */
    public SourceCode process(Resource resource) {
        return sourceCodeSupports().get(resource.extension()).load(resource);
    }

    /**
     * Adds license header to the given {@link SourceCode} only if no existing license header is present there.
     *
     * @param sourceCode source code to add license header to
     * @return source modification result
     */
    public SourceModificationResult addHeader(SourceCode sourceCode) {
        if (sourceCode.metadata().header().isEmpty()) {
            final HeaderCandidate headerCandidate = sourceCode.metadata().headerCandidate();
            final String rendered = renderTemplate(sourceCode);
            final Operation operation = operationForHeaderCandidate(headerCandidate, rendered);

            resourceSplicer.splice(sourceCode.resource().asWritableOrFail(), operation);
            return SourceModificationResult.MODIFIED;    // when header is added, source code is always modified
        } else {
            return SourceModificationResult.NOT_MODIFIED;
        }
    }

    /**
     * Drops license header from given {@link SourceCode}.
     *
     * @param sourceCode source code to remove license header from
     * @return source modification result
     */
    public SourceModificationResult dropHeader(SourceCode sourceCode) {
        if (sourceCode.metadata().header().isPresent()) {
            final LicenseHeader header = sourceCode.metadata().header().get();
            final int startLine = header.startLine() - header.blankLinesBefore();
            final int endLine = header.endLine() + header.blankLinesAfter();
            final Operation operation = Operation.dropSection(startLine, endLine);

            resourceSplicer.splice(sourceCode.resource().asWritableOrFail(), operation);
            return SourceModificationResult.MODIFIED;    // when header is dropped, source code is always modified
        } else {
            return SourceModificationResult.NOT_MODIFIED;
        }
    }

    /**
     * Adds or updates license header in given {@link SourceCode}.
     *
     * @param sourceCode source code to add or update license header in
     * @return source modification result
     */
    public SourceModificationResult updateHeader(SourceCode sourceCode) {
        if (sourceCode.metadata().header().isPresent()) {
            final LicenseHeader header = sourceCode.metadata().header().get();
            final int startLine = header.startLine() - header.blankLinesBefore();
            final int endLine = header.endLine() + header.blankLinesAfter();
            final String rendered = renderTemplate(sourceCode);
            final Operation operation = Operation.replaceSection(startLine, endLine, rendered);

            if (headerUpdateNeeded(rendered, sourceCode)) {
                resourceSplicer.splice(sourceCode.resource().asWritableOrFail(), operation);
                return SourceModificationResult.MODIFIED;
            } else {
                return SourceModificationResult.NOT_MODIFIED;
            }
        } else {
            return addHeader(sourceCode);
        }
    }

    public Map<String, SourceCodeSupport> sourceCodeSupports() {
        return languageIdToSupport.computeIfAbsent(() -> {
            final Set<String> templateNames = templateManager.templates(UserLicenseTemplateSource.TemplateKey.class).keySet().stream()
                .map(UserLicenseTemplateSource.TemplateKey::languageId)
                .collect(Collectors.toSet());

            return sourceCodeSupports.stream()
                .filter(support -> templateNames.contains(support.languageId().value()))  // filter only source codes for which template exists
                .flatMap(support -> support.resourceTypes().stream().map(ext -> Map.entry(ext, support)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        });
    }

    private boolean headerUpdateNeeded(String newHeader, SourceCode sourceCode) {
        if (sourceCode.metadata().header().isPresent()) {
            final LicenseHeader header = sourceCode.metadata().header().get();
            final String lineSeparator = sourceCode.resource().lineSeparator().separator();
            final String prefix = lineSeparator.repeat(header.blankLinesBefore());
            final String suffix = lineSeparator.repeat(header.blankLinesAfter() + 1);   // always keep at least one EOL

            final String currentHeader = header.lines().stream().collect(Collectors.joining(lineSeparator, prefix, suffix));

            return !newHeader.equals(currentHeader);
        } else {
            return true;
        }
    }

    private String renderTemplate(SourceCode sourceCode) {
        final String templateName = sourceCode.languageId().value();
        final Variables variables = configuration.templateVariables().mergeWith(sourceCode.variables());
        final HeaderConfig headerConfig = configuration.headerConfigOrFail(sourceCode.languageId());
        final int blankLinesBefore = headerConfig.headerSpacing().blankLinesBefore();
        final int blankLinesAfter = headerConfig.headerSpacing().blankLinesAfter();
        final LineSeparator lineSeparator = sourceCode.resource().lineSeparator();
        final String lineBreaksBefore = lineSeparator.separator().repeat(blankLinesBefore);
        final String lineBreaksAfter = lineSeparator.separator().repeat(blankLinesAfter);

        // always keep at least one EOL at the end of the rendered template
        final Template template = templateManager.template(new UserLicenseTemplateSource.TemplateKey(templateName));
        final String rendered = template.render(variables).trim() + lineSeparator.separator();

        return lineBreaksBefore + rendered.trim() + lineSeparator.separator() + lineBreaksAfter;
    }

    private Operation operationForHeaderCandidate(HeaderCandidate candidate, String renderedHeader) {
        final int startLine = candidate.putAfterLine() + 1;
        final int endLine = startLine + candidate.blankLinesAfter();

        return (startLine == endLine)
               ? Operation.addSection(startLine, renderedHeader)
               : Operation.replaceSection(startLine, endLine - 1, renderedHeader);
    }
}
