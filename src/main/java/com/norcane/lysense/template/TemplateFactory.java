package com.norcane.lysense.template;


import com.norcane.lysense.resource.Resource;

/**
 * Factory class used to compile template of corresponding <i>template type</i> from the given resource.
 */
public interface TemplateFactory {

    /**
     * Type of the templating language of the template (e.g. {@code mustache}).
     *
     * @return template type
     */
    String templateType();

    /**
     * Compiles template from given {@link Resource}.
     *
     * @param resource resource to load template from
     * @return compiled template
     */
    Template compile(Resource resource);
}
