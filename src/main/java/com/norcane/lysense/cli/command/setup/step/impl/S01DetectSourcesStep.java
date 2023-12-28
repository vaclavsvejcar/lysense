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
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.source.SourceCodeProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class S01DetectSourcesStep implements InstallStep {

    private final ResourceLoader resourceLoader;
    private final SourceCodeProcessor sourceCodeProcessor;

    @Inject
    public S01DetectSourcesStep(ResourceLoader resourceLoader,
                                SourceCodeProcessor sourceCodeProcessor) {

        this.resourceLoader = resourceLoader;
        this.sourceCodeProcessor = sourceCodeProcessor;
    }

    @Override
    public String installationMessage(SetupContext context) {
        final Set<String> sourcePaths = context.get(SetupContextKeys.SOURCE_PATHS);
        return STR."Searching for source code files in @|bold \{sourcePaths}|@";
    }

    @Override
    public void install(SetupContext context) {
        final Set<String> sourcePaths = context.get(SetupContextKeys.SOURCE_PATHS);

        final Set<LanguageId> languageIds = sourcePaths.stream()
                .map(this::detectLanguageIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        context.put(SetupContextKeys.DETECTED_LANGUAGE_IDS, languageIds);
    }

    private Set<LanguageId> detectLanguageIds(String sourcePath) {
        final Set<String> detectedResourceTypes = resourceLoader.resources(sourcePath, _ -> true, true).stream()
                .map(Resource::extension)
                .collect(Collectors.toSet());

        return sourceCodeProcessor.sourceCodeSupports().entrySet().stream()
                .filter(entry -> detectedResourceTypes.contains(entry.getKey()))
                .map(entry -> entry.getValue().languageId())
                .collect(Collectors.toSet());
    }
}
