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
package com.norcane.lysense.source.support;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.HeaderConfig;
import com.norcane.lysense.configuration.api.HeaderStyle;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.CannotReadResourceException;
import com.norcane.lysense.source.HeaderDetectionRules;
import com.norcane.lysense.source.LanguageId;
import com.norcane.lysense.source.SourceCode;
import com.norcane.lysense.source.comment.CommentDetectorFactory;
import com.norcane.lysense.source.metadata.Metadata;
import com.norcane.lysense.source.variables.PatternVariablesExtractor;
import com.norcane.lysense.source.variables.VariablesExtractor;
import com.norcane.lysense.template.Variables;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * Represents support for loading and processing source codes of selected programming/scripting language identified by {@link #languageId()}. To construct new
 * instance, use the <i>staged builder</i> obtained via {@link #builder} method.
 */
public final class SourceCodeSupport {

    private final Configuration configuration;
    private final LanguageId languageId;
    private final Set<String> resourceTypes;
    private final Function<HeaderStyle, CommentDetectorFactory> commentDetectorFactoryFn;
    private final HeaderDetectionRules headerDetectionRules;
    private final VariablesExtractor variablesExtractor;

    private SourceCodeSupport(Configuration configuration,
                              LanguageId languageId,
                              Set<String> resourceTypes,
                              Function<HeaderStyle, CommentDetectorFactory> commentDetectorFactoryFn,
                              HeaderDetectionRules headerDetectionRules,
                              VariablesExtractor variablesExtractor) {

        this.configuration = configuration;
        this.languageId = languageId;
        this.resourceTypes = resourceTypes;
        this.commentDetectorFactoryFn = commentDetectorFactoryFn;
        this.headerDetectionRules = headerDetectionRules;
        this.variablesExtractor = variablesExtractor;
    }

    /**
     * Creates new <i>staged builder</i> for given configuration, <i>language ID</i> and <i>resource types</i>.
     *
     * @param configuration     application configuration
     * @param languageId    unique ID of the supported programming/scripting language
     * @param resourceTypes set of resource types of the programming/scripting language resources
     * @return builder
     */
    public static Builder.CommentDetectorFactoryStep builder(Configuration configuration, LanguageId languageId, Set<String> resourceTypes) {
        return commentDetectorFactory -> headerDetectionRules -> variablesExtractor ->
            new Builder.FinalStep(configuration, languageId, resourceTypes, commentDetectorFactory, headerDetectionRules, variablesExtractor);
    }

    /**
     * Unique identification of the supported programming or scripting language. Please prefer machine-friendly identifiers, such as {@code java} for
     * <i>Java</i> programming language. This ID is later used as a key for specific {@link com.norcane.lysense.configuration.api.HeaderConfig} configuration.
     */
    public LanguageId languageId() {
        return languageId;
    }

    /**
     * <i>Resource types</i> handled by this implementation. Resource type usually means file extension, so for example implementation of support for <i>UNIX
     * Shell</i> might use  {@code Set.of("sh")}.
     */
    public Set<String> resourceTypes() {
        return resourceTypes;
    }

    /**
     * Loads and analyzes source code from given resource. <i>Analysis</i> means trying to find any existing <i>license header</i> and extracting all needed
     * <i>dynamic variables</i>.
     *
     * @param resource resource to load source code from
     * @return loaded and analyzed source code
     * @throws CannotReadResourceException thrown if given resource cannot be read
     */
    public SourceCode load(Resource resource) {
        final CommentDetectorFactory commentDetectorFactory = commentDetectorFactoryFn.apply(configuration.headerConfigOrFail(languageId).headerStyle());

        final List<Variables.Variable> variables = new ArrayList<>();
        final Metadata.Builder metadataBuilder = Metadata.builder(commentDetectorFactory, headerDetectionRules);

        try (final BufferedReader reader = new BufferedReader(resource.reader())) {
            String line;
            while ((line = (reader.readLine())) != null) {
                metadataBuilder.addLine(line);
                variablesExtractor.extract(line).ifPresent(variables::add);
            }
        } catch (IOException e) {
            throw new CannotReadResourceException(resource, e);
        }

        return new SourceCode(languageId, resource, Variables.from(variables), metadataBuilder.build());
    }

    /**
     * <i>Staged builder</i> for the {@link SourceCodeSupport} class.
     */
    public static final class Builder {

        /**
         * First step of the <i>staged builder</i> - get the {@link CommentDetectorFactory}.
         */
        @FunctionalInterface
        public interface CommentDetectorFactoryStep {

            /**
             * Accepts a function that returns {@link CommentDetectorFactory} corresponding to the passed {@link HeaderStyle}.
             *
             * @param commentDetectorFactoryFn function
             * @return next step
             */
            HeaderDetectionRulesStep commentDetectorFactory(Function<HeaderStyle, CommentDetectorFactory> commentDetectorFactoryFn);

            /**
             * Detect only comments of given <i>line syntax</i> pattern in the supported source code.
             *
             * @param lineCommentStart pattern to detect the <i>line syntax</i> comment
             * @return next step
             */
            default HeaderDetectionRulesStep lineHeaderSyntax(Pattern lineCommentStart) {
                return commentDetectorFactory(_ -> CommentDetectorFactory.lineSyntax(lineCommentStart));
            }

            /**
             * Detect comments of either given <i>block syntax</i> or <i>line syntax</i> in the supported source code, based on the
             * {@link HeaderConfig#headerStyle()} defined in the {@link Configuration} passed earlier to this builder.
             *
             * @param blockCommentSyntax describes the possible block comment syntax
             * @param lineCommentSyntax  describes the possible line comment syntax
             * @return next step
             */
            default HeaderDetectionRulesStep configBasedHeaderSyntax(CommentSyntax.BlockCommentSyntax blockCommentSyntax,
                                                                     CommentSyntax.LineCommentSyntax lineCommentSyntax) {

                return commentDetectorFactory(headerStyle -> switch (headerStyle) {
                    case BLOCK_COMMENT -> CommentDetectorFactory.blockSyntax(blockCommentSyntax.start(), blockCommentSyntax.end());
                    case LINE_COMMENT -> CommentDetectorFactory.lineSyntax(lineCommentSyntax.pattern());
                });
            }
        }

        /**
         * Second step of the <i>staged builder</i> - get the {@link HeaderDetectionRules}.
         */
        @FunctionalInterface
        public interface HeaderDetectionRulesStep {

            /**
             * Detect <i>license header</i> in the supported source code based on the given {@link HeaderDetectionRules}.
             *
             * @param headerDetectionRules header detection rules
             * @return next step
             */
            VariablesExtractorStep headerDetectionRules(HeaderDetectionRules headerDetectionRules);

            /**
             * Only detect <i>license header</i> that are placed before the given pattern.
             *
             * @param putBefore pattern before which to detect
             * @return next step
             */
            default VariablesExtractorStep detectHeaderBeforeLine(Pattern putBefore) {
                return headerDetectionRules(HeaderDetectionRules.putBefore(putBefore));
            }

            /**
             * Detect <i>license header</i> between the <i>put after</i> and <i>put before</i> patterns.
             *
             * @param putAfter  pattern after which to detect
             * @param putBefore pattern before which to detect
             * @return next step
             */
            default VariablesExtractorStep detectHeaderAfterAndBeforeLines(Pattern putAfter, Pattern putBefore) {
                return headerDetectionRules(HeaderDetectionRules.from(putAfter, putBefore));
            }
        }

        /**
         * Third step of the <i>staged builder</i> - get the {@link VariablesExtractor}.
         */
        @FunctionalInterface
        public interface VariablesExtractorStep {

            /**
             * Extract <i>dynamic variables</i> from supported source code using the given {@link VariablesExtractor}.
             *
             * @param variablesExtractor variables extractor
             * @return final step
             */
            FinalStep variablesExtractor(VariablesExtractor variablesExtractor);

            /**
             * Extract <i>dynamic variables</i> of given names using the defined patterns.
             *
             * @param variablePatterns variable patterns
             * @return final step
             */
            default FinalStep dynamicVariables(Map<String, Pattern> variablePatterns) {
                return variablesExtractor(PatternVariablesExtractor.from(variablePatterns));
            }

            /**
             * Do not extract any <i>dynamic variables</i>.
             *
             * @return final step
             */
            default FinalStep noDynamicVariables() {
                return variablesExtractor(VariablesExtractor.noExtraction());
            }
        }

        /**
         * Final step of the <i>staged builder</i>.
         */
        public static final class FinalStep {

            private final Configuration configuration;
            private final LanguageId languageId;
            private final Set<String> resourceTypes;
            private final Function<HeaderStyle, CommentDetectorFactory> commentDetectorFactoryFn;
            private final HeaderDetectionRules headerDetectionRules;
            private final VariablesExtractor variablesExtractor;

            public FinalStep(Configuration configuration,
                             LanguageId languageId,
                             Set<String> resourceTypes,
                             Function<HeaderStyle, CommentDetectorFactory> commentDetectorFactoryFn,
                             HeaderDetectionRules headerDetectionRules,
                             VariablesExtractor variablesExtractor) {

                this.configuration = configuration;
                this.languageId = languageId;
                this.resourceTypes = resourceTypes;
                this.commentDetectorFactoryFn = commentDetectorFactoryFn;
                this.headerDetectionRules = headerDetectionRules;
                this.variablesExtractor = variablesExtractor;
            }

            /**
             * Builds new instance of the {@link SourceCodeSupport}.
             *
             * @return new instance
             */
            public SourceCodeSupport build() {
                return new SourceCodeSupport(configuration, languageId, resourceTypes, commentDetectorFactoryFn, headerDetectionRules, variablesExtractor);
            }
        }

        /**
         * Represents the comment syntax.
         */
        public sealed interface CommentSyntax permits CommentSyntax.BlockCommentSyntax, CommentSyntax.LineCommentSyntax {

            /**
             * Block comment syntax (either single or multi-line).
             *
             * @param start pattern to detect start of the comment
             * @param end   pattern to detect end of the comment
             */
            record BlockCommentSyntax(Pattern start, Pattern end) implements CommentSyntax {
            }

            /**
             * Line comment syntax.
             *
             * @param pattern pattern to detect the comment
             */
            record LineCommentSyntax(Pattern pattern) implements CommentSyntax {
            }

            /**
             * Block comment syntax (either single or multi-line).
             *
             * @param start pattern to detect start of the comment
             * @param end   pattern to detect end of the comment
             */
            static BlockCommentSyntax blockComment(Pattern start, Pattern end) {
                return new BlockCommentSyntax(start, end);
            }

            /**
             * Line comment syntax.
             *
             * @param pattern pattern to detect the comment
             */
            static LineCommentSyntax lineComment(Pattern pattern) {
                return new LineCommentSyntax(pattern);
            }
        }
    }
}
