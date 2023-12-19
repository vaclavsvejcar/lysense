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
package com.norcane.lysense.template;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.template.source.TemplateSource;
import com.norcane.toolkit.state.Stateful;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static com.norcane.toolkit.Prelude.nonNullOrThrow;
import static com.norcane.toolkit.Prelude.toMap;

/**
 * Component responsible for dynamically loading and compiling <i>templates</i> from various <i>template sources</i>. Each template source is identified by the
 * type of its <i>template key</i> and each template within the template source is identified by its <i>template key</i>. This implementation is lazy - template
 * source is loaded only when any template from it is requested and template is compiled only when obtained.
 *
 * @see Template
 * @see TemplateSource
 */
@ApplicationScoped
public class TemplateManager implements Stateful {

    private final Map<String, TemplateFactory> templateFactories;
    private final Map<Class<? extends TemplateKey>, TemplateSource<?>> templateSources;
    private final Set<Class<? extends TemplateKey>> loadedTemplateSources = new HashSet<>();
    private final Map<TemplateKey, Resource> rawTemplates = new HashMap<>();
    private final Map<TemplateKey, Template> compiledTemplates = new HashMap<>();

    @Inject
    public TemplateManager(Instance<TemplateFactory> templateFactories, Instance<TemplateSource<?>> templateSources) {

        this.templateFactories = toMap(TemplateFactory::templateType, templateFactories);
        this.templateSources = toMap(TemplateSource::templateKeyClass, templateSources);
    }

    /**
     * Returns template identified by its {@link TemplateKey}. The template is compiled (if not already done) and cached for future use.
     *
     * @param templateKey template key
     * @return compiled template
     * @throws IllegalArgumentException if template with given key does not exist
     */
    public Template template(TemplateKey templateKey) {
        compileTemplate(templateKey);

        return compiledTemplates.get(templateKey);
    }

    /**
     * Returns a map of all templates available via {@link TemplateSource} identified by its {@link TemplateKey}. If the template source is not loaded yet, it
     * will be loaded and cached for further use.
     *
     * @param templateKey template key class identifying the {@link TemplateSource}
     * @param filter      filter that allows to choose only templates with template key matching the predicate
     * @param <K>         template key type
     * @return map of templates
     */
    public <K extends TemplateKey> Map<K, Template> templates(Class<K> templateKey, Predicate<K> filter) {
        loadRawTemplates(templateKey);

        return rawTemplates.keySet().stream()
            .filter(templateKey::isInstance)
            .map(templateKey::cast)
            .filter(filter)
            .collect(Collectors.toMap(k -> k, this::template));
    }

    /**
     * Returns a map of all templates available via {@link TemplateSource} identified by its {@link TemplateKey}. If the template source is not loaded yet, it
     * will be loaded and cached for further use.
     *
     * @param templateKey template key class identifying the {@link TemplateSource}
     * @param <K>         template key type
     * @return map of templates
     */
    public <K extends TemplateKey> Map<K, Template> templates(Class<K> templateKey) {
        return templates(templateKey, _ -> true);
    }

    /**
     * Returns a set of all template sources, represented by their {@link TemplateKey} classes, that were dynamically loaded so far.
     *
     * @return set of loaded template sources
     */
    public Set<Class<? extends TemplateKey>> loadedTemplateSources() {
        return Collections.unmodifiableSet(loadedTemplateSources);
    }

    @Override
    public void resetState() {
        Stateful.super.resetState();

        loadedTemplateSources.clear();
        rawTemplates.clear();
        compiledTemplates.clear();
    }

    private void compileTemplate(TemplateKey templateKey) {
        loadRawTemplates(templateKey.getClass());

        final Resource rawTemplate = nonNullOrThrow(rawTemplates.get(templateKey), STR."No raw template found for template key '\{templateKey}'");
        final TemplateFactory templateFactory = nonNullOrThrow(templateFactories.get(rawTemplate.extension()),
                                                               STR."No template factory found for template type '\{rawTemplate.extension()}'");
        final Template template = templateFactory.compile(rawTemplate);

        compiledTemplates.put(templateKey, template);
    }

    private <K extends TemplateKey> void loadRawTemplates(Class<K> templateKey) {
        if (loadedTemplateSources.contains(templateKey)) {
            return;
        }

        final TemplateSource<?> templateSource = nonNullOrThrow(templateSources.get(templateKey),
                                                                STR."No template source found for template key: \{templateKey}");

        loadedTemplateSources.add(templateKey);
        rawTemplates.putAll(templateSource.templateResources());
    }
}
