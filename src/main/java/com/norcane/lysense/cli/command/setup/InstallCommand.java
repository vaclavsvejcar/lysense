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
package com.norcane.lysense.cli.command.setup;

import com.norcane.lysense.cli.ReturnCode;
import com.norcane.lysense.cli.command.CliCommand;
import com.norcane.lysense.cli.command.setup.context.SetupContext;
import com.norcane.lysense.cli.command.setup.step.InstallStep;
import com.norcane.lysense.cli.command.setup.step.impl.SetupContextKeys;
import com.norcane.lysense.meta.ProductInfo;
import com.norcane.lysense.meta.RuntimeInfo;
import com.norcane.lysense.ui.alert.Alert;
import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.ProgressBar;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(
    name = "install",
    description = "install and configure zen in current project",
    usageHelpAutoWidth = true,
    headerHeading = "@|bold,underline Usage|@:%n%n",
    descriptionHeading = "%n@|bold,underline Description|@:%n%n",
    parameterListHeading = "%n@|bold,underline Parameters|@:%n",
    optionListHeading = "%n@|bold,underline Options|@:%n"
)
public class InstallCommand extends CliCommand {

    private final Instance<InstallStep> installSteps;
    private final RuntimeInfo runtimeInfo;

    @CommandLine.Option(
        names = {"-s", "--source"},
        description = "specify path to source code file/directory",
        paramLabel = "PATH",
        required = true
    )
    Set<String> sourcePaths;

    @Inject
    public InstallCommand(Console console,
                          Instance<InstallStep> installSteps,
                          RuntimeInfo runtimeInfo) {

        super(console);
        this.installSteps = installSteps;
        this.runtimeInfo = runtimeInfo;
    }

    @Override
    protected ReturnCode execute() {
        console.render(Alert.info(STR."Beginning installing \{ProductInfo.NAME}"));

        final SetupContext context = new SetupContext();
        final List<InstallStep> orderedInstallSteps = orderedInstallSteps();

        // initialize context
        context.put(SetupContextKeys.TEMPLATES_DIR, runtimeInfo.generatedTemplatesPath());
        context.put(SetupContextKeys.SOURCE_PATHS, sourcePaths);

        for (final InstallStep step : ProgressBar.checkList(orderedInstallSteps,
                                                            step -> step.installationMessage(context),
                                                            console)) {

            step.install(context);
        }

        return ReturnCode.SUCCESS;
    }

    List<InstallStep> orderedInstallSteps() {
        return installSteps.stream()
            .sorted(Comparator.comparing(installStep -> installStep.getClass().getSimpleName()))
            .toList();
    }
}
