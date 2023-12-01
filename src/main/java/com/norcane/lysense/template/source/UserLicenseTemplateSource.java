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