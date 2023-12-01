package com.norcane.lysense.template.mustache;

import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.template.Template;
import com.norcane.lysense.template.TemplateFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implementation of {@link TemplateFactory} to support <a href="https://mustache.github.io/">Mustache</a> templates. Uses {@code mustache} as a <i>template
 * type</i>.
 *
 * @see MustacheTemplate
 */
@ApplicationScoped
public class MustacheTemplateFactory implements TemplateFactory {

    @Override
    public String templateType() {
        return "mustache";
    }

    @Override
    public Template compile(Resource resource) {
        return MustacheTemplate.compile(resource);
    }
}
