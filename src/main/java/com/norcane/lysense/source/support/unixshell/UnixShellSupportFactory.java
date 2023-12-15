package com.norcane.lysense.source.support.unixshell;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.source.support.SourceCodeSupport;
import com.norcane.toolkit.InstanceFactory;

import java.util.Set;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;


/**
 * Implementation of {@link SourceCodeSupport} to support managing license headers in <i>UNIX Shell</i> scripts.
 */
@ApplicationScoped
public class UnixShellSupportFactory implements InstanceFactory<SourceCodeSupport> {

    private final Configuration configuration;

    @Inject
    public UnixShellSupportFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @Produces
    @ApplicationScoped
    public SourceCodeSupport instance() {
        return SourceCodeSupport

            // languageId is 'unix-shell', file extensions are 'sh'
            .builder(configuration, "unix-shell", Set.of("sh"))

            // use line comment syntax for license headers
            .lineHeaderSyntax(Pattern.compile("^#(?!!)"))

            // detect header after hashbang and before any actual code
            .detectHeaderAfterAndBeforeLines(Pattern.compile("^#!"), Pattern.compile("^(?!#).*"))

            // no dynamic variables extraction
            .noDynamicVariables()

            // build the instance
            .build();
    }
}
