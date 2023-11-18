package com.norcane.lysense;

import io.quarkus.picocli.runtime.PicocliCommandLineFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import picocli.CommandLine;

@ApplicationScoped
public class QuarkusConfiguration {

    @Produces
    CommandLine customCommandLine(PicocliCommandLineFactory factory) {
        return factory.create()
            .setCaseInsensitiveEnumValuesAllowed(true);
    }
}
