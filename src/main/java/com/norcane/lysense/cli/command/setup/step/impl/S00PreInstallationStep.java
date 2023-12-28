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

import com.norcane.lysense.cli.command.exception.ProductAlreadyInstalledException;
import com.norcane.lysense.cli.command.setup.context.SetupContext;
import com.norcane.lysense.cli.command.setup.step.InstallStep;
import com.norcane.lysense.configuration.ConfigurationLookup;
import com.norcane.lysense.configuration.ConfigurationManager;
import com.norcane.toolkit.io.FileSystem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class S00PreInstallationStep implements InstallStep {

    private final ConfigurationManager configurationManager;
    private final FileSystem fileSystem;

    @Inject
    public S00PreInstallationStep(ConfigurationManager configurationManager,
                                  FileSystem fileSystem) {

        this.configurationManager = configurationManager;
        this.fileSystem = fileSystem;
    }

    @Override
    public String installationMessage(SetupContext context) {
        return STR."Checking if product is already installed in @|bold \{fileSystem.currentDirectory()}|@";
    }

    @Override
    public void install(SetupContext context) {
        if (configurationManager.findConfigurationResource() instanceof ConfigurationLookup.Found found) {
            throw new ProductAlreadyInstalledException(found.resource());
        }
    }
}
