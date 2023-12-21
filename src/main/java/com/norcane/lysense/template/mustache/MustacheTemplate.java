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
package com.norcane.lysense.template.mustache;

import com.github.mustachejava.*;
import com.github.mustachejava.reflect.GuardedBinding;
import com.github.mustachejava.reflect.MissingWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import com.google.common.base.Throwables;
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
