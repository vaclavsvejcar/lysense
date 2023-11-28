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

        final Map<String, List<Resource>> templateNameToResource = resources().stream()
            .collect(Collectors.groupingBy(Resource::name));

        return templateNameToResource.entrySet().stream()
            .map(entry -> {
                final String templateName = entry.getKey();
                final List<Resource> templateResources = entry.getValue();

                // found multiple template resources with same template name
                if (templateResources.size() > 1) {
                    final List<URI> paths = templateResources.stream().map(Resource::uri).toList();
                    throw new DuplicateTemplatesFoundException(templateName, paths);
                }

                final Resource templateResource = templateResources.get(0);
                return Map.entry(templateKey(templateResource), templateResource);
            })
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                Collections::unmodifiableMap
            ));
    }
}
