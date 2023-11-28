package com.norcane.lysense.configuration.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.meta.SemVer;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public record Configuration(@JsonProperty("base-version") @NotNull SemVer baseVersion,
                            @JsonProperty("templates") @NotEmpty List<String> templates) {
}
