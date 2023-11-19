package com.norcane.lysense.template.mustache;

import com.google.common.base.Throwables;

import com.github.mustachejava.Binding;
import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.reflect.GuardedBinding;
import com.github.mustachejava.reflect.MissingWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.template.Template;
import com.norcane.lysense.template.Variables;
import com.norcane.lysense.template.exception.MissingTemplateVariableException;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;

/**
 * Implementation of {@link Template} that represents <a href="https://mustache.github.io/">Mustache</a> template. Uses {@code mustache} as a <i>template
 * type</i>.
 */
public final class MustacheTemplate implements Template {

    private static final MustacheFactory mustacheFactory = missingVariablesAwareMustacheFactory();

    private final Resource resource;
    private final Mustache compiled;

    private MustacheTemplate(Resource resource, Mustache compiled) {
        this.resource = resource;
        this.compiled = compiled;
    }

    /**
     * Compiles template from given {@link Resource}.
     *
     * @param resource resource to load template from
     * @return compiled template
     * @throws UncheckedIOException if an I/O error occurs
     */
    public static MustacheTemplate compile(Resource resource) {
        try (final Reader reader = resource.reader()) {
            return new MustacheTemplate(resource, mustacheFactory.compile(reader, resource.uri().toString()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Resource resource() {
        return resource;
    }

    @Override
    public Writer render(Writer writer, Variables variables) {
        try {
            compiled.execute(writer, variables.toMap());
            writer.flush();
        } catch (MustacheException ex) {
            // unwrap and rethrow the root cause if it has been thrown by this app
            if (Throwables.getRootCause(ex) instanceof ApplicationException zre) {
                throw zre;
            }

            throw ex;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return writer;
    }

    // Tinkers the Mustache library in order to be able to detect missing variables in templates, which isn't possible out of the box.
    // https://github.com/spullara/mustache.java/issues/1
    // https://github.com/spullara/mustache.java/issues/226
    // https://github.com/spullara/mustache.java/issues/287
    private static MustacheFactory missingVariablesAwareMustacheFactory() {
        final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
        mustacheFactory.setObjectHandler(new ReflectionObjectHandler() {
            @Override
            public Binding createBinding(String name, TemplateContext tc, Code code) {
                return new GuardedBinding(this, name, tc, code) {
                    @Override
                    protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                        final Wrapper wrapper = super.getWrapper(name, scopes);

                        if (wrapper instanceof MissingWrapper) {
                            throw new MissingTemplateVariableException(tc.file(), name);
                        }

                        return wrapper;
                    }
                };
            }
        });

        return mustacheFactory;
    }
}
