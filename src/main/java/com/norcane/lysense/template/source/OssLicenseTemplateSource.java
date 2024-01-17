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

import com.norcane.lysense.domain.LanguageId;
import com.norcane.lysense.domain.LicenseId;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.template.TemplateFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static com.norcane.lysense.domain.LanguageId.languageId;
import static com.norcane.lysense.domain.LicenseId.licenseId;
import static com.norcane.toolkit.Prelude.toMap;

/**
 * Implementation of {@link TemplateSource} that provides access to built-in <i>open source licenses</i> (such as
 * <i>Apache 2.0</i>, <i>MIT</i>, etc.). Current implementation expects that all OSS licenses are located in
 * {@code classpath:/embedded/oss-licenses/{license-id}/{language-id}.{template-type}}.
 */
@ApplicationScoped
public class OssLicenseTemplateSource extends TemplateSource<OssLicenseTemplateSource.TemplateKey> {

    private static final String OSS_LICENSES_PATH = "classpath:/embedded/oss-licenses/**";

    private final ResourceLoader resourceLoader;
    private final Map<String, TemplateFactory> templateFactories;

    @Inject
    public OssLicenseTemplateSource(Instance<TemplateFactory> templateFactories,
                                    ResourceLoader resourceLoader) {

        this.resourceLoader = resourceLoader;
        this.templateFactories = toMap(TemplateFactory::templateType, templateFactories);

    }

    @Override
    public Class<TemplateKey> templateKeyClass() {
        return OssLicenseTemplateSource.TemplateKey.class;
    }

    @Override
    protected TemplateKey templateKey(Resource resource) {
        if (resource.parent().isEmpty()) {
            throw new IllegalArgumentException(STR."Invalid OSS license template path: \{resource.uri()}");
        }

        final String path = resource.parent().get();
        final int lastSlashIndex = path.lastIndexOf('/');
        final String licenseId = lastSlashIndex != -1 ? path.substring(lastSlashIndex + 1) : path;

        return new TemplateKey(licenseId(licenseId), languageId(resource.name()));
    }

    @Override
    protected List<Resource> resources() {
        final Set<String> templateTypes = templateFactories.keySet();
        final Predicate<Resource> filter = resource -> templateTypes.contains(resource.extension());

        return resourceLoader.resources(OSS_LICENSES_PATH, filter, true);
    }

    /**
     * Template key for <i>OSS license</i> template.
     *
     * @param licenseId  OSS license ID (e.g. {@code bsd3})
     * @param languageId language ID (e.g. {@code java})
     */
    public record TemplateKey(LicenseId licenseId, LanguageId languageId)
        implements com.norcane.lysense.template.TemplateKey {
    }
}
