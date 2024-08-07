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
package com.norcane.lysense.cli.command;

import com.google.common.base.Stopwatch;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.configuration.ConfigurationManager;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.RunMode;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.source.SourceCode;
import com.norcane.lysense.source.SourceCodeProcessor;
import com.norcane.lysense.template.TemplateManager;
import com.norcane.lysense.template.source.UserLicenseTemplateSource;
import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import picocli.CommandLine;

import static com.norcane.toolkit.Prelude.when;

@CommandLine.Command(
    name = "run",
    description = "add, drop or update license headers",
    usageHelpAutoWidth = true,
    headerHeading = "@|bold,underline Usage|@:%n%n",
    descriptionHeading = "%n@|bold,underline Description|@:%n%n",
    parameterListHeading = "%n@|bold,underline Parameters|@:%n",
    optionListHeading = "%n@|bold,underline Options|@:%n"
)
public class RunCommand extends CliCommand {

    private final Configuration configuration;
    private final ConfigurationManager configurationManager;
    private final ResourceLoader resourceLoader;
    private final SourceCodeProcessor sourceCodeProcessor;
    private final TemplateManager templateManager;

    @CommandLine.Option(
        names = {"-m", "--mode"},
        description = "run mode, available values: ${COMPLETION-CANDIDATES}",
        paramLabel = "MODE"
    )
    RunMode cliRunMode;

    @Inject
    public RunCommand(Console console,
                      Configuration configuration,
                      ConfigurationManager configurationManager,
                      ResourceLoader resourceLoader,
                      SourceCodeProcessor sourceCodeProcessor,
                      TemplateManager templateManager) {

        super(console);
        this.configuration = configuration;
        this.configurationManager = configurationManager;
        this.resourceLoader = resourceLoader;
        this.sourceCodeProcessor = sourceCodeProcessor;
        this.templateManager = templateManager;
    }

    @Override
    protected ReturnCode execute() {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        final RunMode runMode = cliRunMode != null ? cliRunMode : configuration.runMode();

        console.printLn("Loaded configuration from " + configurationManager.configurationRef().resource().uri());

        final List<SourceCode> sourceCodes = loadSourceCodes();
        console.printLn("Found @|bold %s|@ source code files from @|bold %s|@".formatted(sourceCodes.size(), configuration.sources()));

        final RunResult runResult = switch (runMode) {
            case ADD -> addHeaders(sourceCodes);
            case DROP -> dropHeaders(sourceCodes);
            case UPDATE -> updateHeaders(sourceCodes);
        };

        stopwatch.stop();
        console.emptyLine();
        console.printLn("Modified @|bold %s|@ source code file(s) in @|bold %s|@".formatted(runResult.modifiedSources().size(), stopwatch));

        return ReturnCode.SUCCESS;
    }

    private RunResult addHeaders(List<SourceCode> sourceCodes) {
        final List<SourceCode> modifiedSources = new ArrayList<>();

        final Function<SourceCode, String> messageFn =
            sourceCode -> sourceCode.metadata().header().isEmpty()
                          ? "Adding header to @|bold %s|@".formatted(sourceCode.resource().uri())
                          : "Header already present in @|bold %s}|@".formatted(sourceCode.resource().uri());

        for (final SourceCode sourceCode : ProgressBar.concise(sourceCodes, messageFn, console)) {
            when(sourceCodeProcessor.addHeader(sourceCode).modified(), () -> modifiedSources.add(sourceCode));
        }

        return new RunResult(ReturnCode.SUCCESS, Collections.unmodifiableList(modifiedSources));
    }

    private RunResult dropHeaders(List<SourceCode> sourceCodes) {
        final List<SourceCode> modifiedSources = new ArrayList<>();

        final Function<SourceCode, String> messageFn =
            sourceCode -> sourceCode.metadata().header().isPresent()
                          ? "Dropping header from @|bold %s|@".formatted(sourceCode.resource().uri())
                          : "No header present in @|bold %s|@".formatted(sourceCode.resource().uri());

        for (final SourceCode sourceCode : ProgressBar.concise(sourceCodes, messageFn, console)) {
            when(sourceCodeProcessor.dropHeader(sourceCode).modified(), () -> modifiedSources.add(sourceCode));
        }

        return new RunResult(ReturnCode.SUCCESS, Collections.unmodifiableList(modifiedSources));
    }

    private RunResult updateHeaders(List<SourceCode> sourceCodes) {
        final List<SourceCode> modifiedSources = new ArrayList<>();

        final Function<SourceCode, String> messageFn =
            sourceCode -> sourceCode.metadata().header().isPresent()
                          ? "Updating header in @|bold %s|@".formatted(sourceCode.resource().uri())
                          : "Adding header to @|bold %s|@".formatted(sourceCode.resource().uri());

        for (final SourceCode sourceCode : ProgressBar.concise(sourceCodes, messageFn, console)) {
            when(sourceCodeProcessor.updateHeader(sourceCode).modified(), () -> modifiedSources.add(sourceCode));
        }

        return new RunResult(ReturnCode.SUCCESS, Collections.unmodifiableList(modifiedSources));
    }

    private List<SourceCode> loadSourceCodes() {
        final Set<String> resourceExtensions = sourceCodeExtensions();
        final Predicate<Resource> filter = resource -> resourceExtensions.contains(resource.extension());

        return configuration.sources().stream()
            .map(sourcePath -> resourceLoader.resources(sourcePath, filter, true))
            .flatMap(Collection::stream)
            .map(Resource::asWritableOrFail)
            .map(sourceCodeProcessor::process)
            .toList();
    }

    private Set<String> sourceCodeExtensions() {
        final Set<String> templateNames = templateManager.templates(UserLicenseTemplateSource.TemplateKey.class).keySet().stream()
            .map(UserLicenseTemplateSource.TemplateKey::languageId)
            .collect(Collectors.toSet());

        return sourceCodeProcessor.sourceCodeSupports().entrySet().stream()
            .filter(entry -> templateNames.contains(entry.getValue().languageId().value()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private record RunResult(ReturnCode returnCode, List<SourceCode> modifiedSources) {
    }
}
