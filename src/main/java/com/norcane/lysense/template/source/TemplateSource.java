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

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.template.TemplateKey;
import com.norcane.lysense.template.exception.DuplicateTemplatesFoundException;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a source of template resources, identified by a {@link TemplateKey}, that can be loaded and compiled.
 *
 * @param <K> type of template key
 */
public abstract class TemplateSource<K extends TemplateKey> {

    /**
     * Class of the {@link TemplateKey} used by templates of this template source.
     *
     * @return template key class
     */
    public abstract Class<K> templateKeyClass();

    /**
     * Returns <i>template key</i> that will uniquely identify the given <i>template resource</i>.
     *
     * @param resource template resource
     * @return template key
     */
    protected abstract K templateKey(Resource resource);

    /**
     * Returns resources of all templates available within this template source.
     *
     * @return template resources
     */
    protected abstract List<Resource> resources();

    /**
     * Returns resources of all templates available within this template source, grouped by their template key. Also check that there are no templates with
     * duplicate template key.
     *
     * @return template resources
     * @throws DuplicateTemplatesFoundException if there are multiple templates with same template key
     */
    public final Map<K, Resource> templateResources() {
        final Map<K, List<Resource>> resourcesByKey = resources().stream()
            .collect(Collectors.groupingBy(this::templateKey, Collectors.mapping(resource -> resource, Collectors.toList())));

        return resourcesByKey.entrySet().stream()
            .map(entry -> {
                final K templateKey = entry.getKey();
                final List<Resource> templateResources = entry.getValue();

                // found multiple template resources with same template name
                if (templateResources.size() > 1) {
                    final List<URI> paths = templateResources.stream().map(Resource::uri).toList();
                    throw new DuplicateTemplatesFoundException(templateKey.toString(), paths);
                }

                final Resource templateResource = templateResources.getFirst();
                return Map.entry(templateKey, templateResource);

            })
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                Collections::unmodifiableMap
            ));
    }
}
