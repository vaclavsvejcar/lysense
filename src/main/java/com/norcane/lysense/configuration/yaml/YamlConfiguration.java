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
package com.norcane.lysense.configuration.yaml;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.RunMode;
import com.norcane.lysense.meta.SemVer;
import com.norcane.lysense.source.LanguageId;
import com.norcane.lysense.template.Variables;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

@RegisterForReflection
public class YamlConfiguration implements Configuration {

    @NotNull
    @JsonProperty("base-version")
    private SemVer baseVersion;

    @NotNull
    @JsonProperty("run-mode")
    private RunMode runMode;

    @NotEmpty
    @JsonProperty("templates")
    private List<String> templates;

    @NotEmpty
    @JsonProperty("sources")
    private List<String> sources;

    @JsonMerge
    @Valid
    @NotEmpty
    @JsonProperty("license-headers")
    private Map<LanguageId, YamlHeaderConfig> headerConfigs;

    @NotNull
    @JsonProperty("template-variables")
    private Variables templateVariables;

    @Override
    public SemVer baseVersion() {
        return baseVersion;
    }

    @Override
    public RunMode runMode() {
        return runMode;
    }

    @Override
    public List<String> templates() {
        return templates;
    }

    @Override
    public List<String> sources() {
        return sources;
    }

    @Override
    public Map<LanguageId, YamlHeaderConfig> headerConfigs() {
        return headerConfigs;
    }

    @Override
    public Variables templateVariables() {
        return templateVariables;
    }
}
