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
package com.norcane.lysense.template.source;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.template.TemplateFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static com.norcane.toolkit.Prelude.toMap;

/**
 * Implementation of {@link TemplateSource} that provides access to user-defined <i>license header templates</i> from path(s) specified by
 * {@link Configuration#templates()}} and {@link Resource#extension()} as <i>template type</i>>.
 *
 * <br><br>
 *
 * For example, if templates are loaded from file system, then file name will be used as <i>template name</i> and file extension as <i>template type</i>:
 * <pre>
 *     /foo/bar/my-awesome-template.mustache
 *              │                   │
 *              └─ 'my-awesome-template' is the template name
 *                                  │
 *                                  └─ 'mustache' is the template type
 * </pre>
 */
@ApplicationScoped
public class UserLicenseTemplateSource extends TemplateSource<UserLicenseTemplateSource.TemplateKey> {

    private final Configuration configuration;
    private final ResourceLoader resourceLoader;
    private final Map<String, TemplateFactory> templateTypeToFactory;


    @Inject
    public UserLicenseTemplateSource(Instance<TemplateFactory> templateFactories,
                                     Configuration configuration,
                                     ResourceLoader resourceLoader) {

        this.configuration = configuration;
        this.resourceLoader = resourceLoader;
        this.templateTypeToFactory = toMap(TemplateFactory::templateType, templateFactories);
    }

    @Override
    public Class<TemplateKey> templateKeyClass() {
        return UserLicenseTemplateSource.TemplateKey.class;
    }

    @Override
    protected TemplateKey templateKey(Resource resource) {
        return new TemplateKey(resource.name());
    }

    @Override
    protected List<Resource> resources() {
        final List<String> templatePaths = configuration.templates();
        final Set<String> templateTypes = templateTypeToFactory.keySet();

        final Predicate<Resource> filter = resource -> templateTypes.contains(resource.extension());

        return templatePaths.stream()
            .map(location -> resourceLoader.resources(location, filter, true))
            .flatMap(Collection::stream)
            .toList();
    }

    /**
     * Represents a <i>template key</i> that uniquely identifies a <i>license header template</i> within {@link UserLicenseTemplateSource}.
     *
     * @param languageId unique identifier of the source code language for which this template is used
     */
    public record TemplateKey(String languageId) implements com.norcane.lysense.template.TemplateKey {
    }
}
