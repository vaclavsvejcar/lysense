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

import static com.norcane.lysense.domain.LanguageId.languageId;
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
