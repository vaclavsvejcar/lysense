package com.norcane.lysense.configuration.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.meta.SemVer;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public record YamlConfiguration(@JsonProperty("base-version") @NotNull SemVer baseVersion,
                                @JsonProperty("templates") @NotEmpty List<String> templates)

    implements Configuration {
}
