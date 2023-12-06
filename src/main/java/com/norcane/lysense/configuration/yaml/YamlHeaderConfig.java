package com.norcane.lysense.configuration.yaml;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.configuration.api.HeaderConfig;
import com.norcane.lysense.configuration.api.HeaderStyle;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public class YamlHeaderConfig implements HeaderConfig {

    @Valid
    @NotNull
    @JsonProperty("header-style")
    private HeaderStyle headerStyle;

    @Valid
    @NotNull
    @JsonMerge
    @JsonProperty("header-spacing")
    private YamlHeaderSpacing headerSpacing;

    @Override
    public HeaderStyle headerStyle() {
        return headerStyle;
    }

    @Override
    public YamlHeaderSpacing headerSpacing() {
        return headerSpacing;
    }
}
