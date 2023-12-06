package com.norcane.lysense.configuration.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.configuration.api.HeaderSpacing;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public class YamlHeaderSpacing implements HeaderSpacing {

    @NotNull
    @JsonProperty("blank-lines-after")
    private Integer blankLinesAfter;

    @NotNull
    @JsonProperty("blank-lines-before")
    private Integer blankLinesBefore;

    @Override
    public Integer blankLinesAfter() {
        return blankLinesAfter;
    }

    @Override
    public Integer blankLinesBefore() {
        return blankLinesBefore;
    }
}
