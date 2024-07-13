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
package com.norcane.lysense.cli.command.setup.step.impl;

import com.norcane.lysense.cli.command.setup.context.SetupContext;
import com.norcane.lysense.cli.command.setup.step.InstallStep;
import com.norcane.lysense.domain.LanguageId;
import com.norcane.lysense.meta.RuntimeInfo;
import com.norcane.lysense.template.TemplateManager;
import com.norcane.lysense.template.source.OssLicenseTemplateSource;
import com.norcane.toolkit.io.FileSystem;

import java.nio.file.Path;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class S02CopyTemplatesStep implements InstallStep {

    private final FileSystem fileSystem;
    private final RuntimeInfo runtimeInfo;
    private final TemplateManager templateManager;

    @Inject
    public S02CopyTemplatesStep(FileSystem fileSystem,
                                RuntimeInfo runtimeInfo, TemplateManager templateManager) {

        this.fileSystem = fileSystem;
        this.runtimeInfo = runtimeInfo;
        this.templateManager = templateManager;
    }

    @Override
    public String installationMessage(SetupContext context) {
        return "Generating templates to " + context.get(SetupContextKeys.TEMPLATES_DIR);
    }

    @Override
    public void install(SetupContext context) {
        final Path templatesDir = context.get(SetupContextKeys.TEMPLATES_DIR);
        final Set<LanguageId> detectedLanguageIds = context.get(SetupContextKeys.DETECTED_LANGUAGE_IDS);

        ensureTemplatesDirectoryExists(templatesDir);
        copyOssTemplates(templatesDir, detectedLanguageIds);
    }

    private void ensureTemplatesDirectoryExists(Path templatesDir) {
        fileSystem.createDirectory(templatesDir);
    }

    private void copyOssTemplates(Path templatesDir, Set<LanguageId> detectedLanguageIds) {
        templateManager
            .templates(OssLicenseTemplateSource.TemplateKey.class, templateKey -> detectedLanguageIds.contains(templateKey.languageId()))
            .forEach((templateKey, template) -> {

                // write template to <templatesDir>/<languageId>.<extension> (e.g. /foo/bar/java.mustache)
                final String templateName = templateKey.languageId().value() + "." + template.resource().extension();
                final Path templateFile = templatesDir.resolve(templateName);
                fileSystem.write(template.resource(), templateFile);
            });
    }
}
