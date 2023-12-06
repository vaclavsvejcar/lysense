package com.norcane.lysense.configuration.yaml;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.meta.SemVer;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public class YamlConfiguration implements Configuration {

    @NotNull
    @JsonProperty("base-version")
    private SemVer baseVersion;

    @NotEmpty
    @JsonProperty("templates")
    private List<String> templates;

    @JsonMerge
    @Valid
    @NotEmpty
    @JsonProperty("license-headers")
    private Map<String, YamlHeaderConfig> headerConfigs;

    @Override
    public SemVer baseVersion() {
        return baseVersion;
    }

    @Override
    public List<String> templates() {
        return templates;
    }

    @Override
    public Map<String, YamlHeaderConfig> headerConfigs() {
        return headerConfigs;
    }

}
