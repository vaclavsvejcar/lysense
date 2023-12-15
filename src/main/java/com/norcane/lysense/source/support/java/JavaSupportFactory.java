package com.norcane.lysense.source.support.java;


import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.source.support.SourceCodeSupport;
import com.norcane.toolkit.InstanceFactory;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import static com.norcane.lysense.source.LanguageId.languageId;
import static com.norcane.lysense.source.support.SourceCodeSupport.Builder.CommentSyntax.blockComment;
import static com.norcane.lysense.source.support.SourceCodeSupport.Builder.CommentSyntax.lineComment;


/**
 * Produces instance of{@link SourceCodeSupport} to support managing license headers in <i>Java</i> source code. This implementation extracts following
 * <i>dynamic variables</i> from analyzed source codes:
 *
 * <ul>
 *     <li><code>_java.package_name</code> - package name of the current source code</li>
 * </ul>
 */
@ApplicationScoped
public class JavaSupportFactory implements InstanceFactory<SourceCodeSupport> {

    private final Configuration configuration;

    @Inject
    public JavaSupportFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @Produces
    @ApplicationScoped
    public SourceCodeSupport instance() {
        return SourceCodeSupport
            .builder(configuration, languageId("java"), Set.of("java"))

            // use either block or line comment, based on configuration
            .configBasedHeaderSyntax(
                blockComment(Pattern.compile("^/\\*(?!\\*)"), Pattern.compile("\\*/$")),
                lineComment(Pattern.compile("^//"))
            )

            // detect header only before package declaration
            .detectHeaderBeforeLine(Pattern.compile("^package"))

            // extract dynamic variables
            .dynamicVariables(Map.of(
                "_java.package_name", Pattern.compile("^package (.*);$")
            ))

            // build the instance
            .build();
    }
}
