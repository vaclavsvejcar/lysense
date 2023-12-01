package com.norcane.lysense.configuration.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.norcane.lysense.meta.SemVer;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record BaseVersionWrapper(@JsonProperty("base-version") SemVer baseVersion) {
}
