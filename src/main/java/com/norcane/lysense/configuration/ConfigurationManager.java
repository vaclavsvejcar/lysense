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
package com.norcane.lysense.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.ConfigurationRef;
import com.norcane.lysense.configuration.api.HeaderStyle;
import com.norcane.lysense.configuration.api.RunMode;
import com.norcane.lysense.configuration.exception.*;
import com.norcane.lysense.configuration.serialization.LowerCaseDashSeparatedEnumDeserializer;
import com.norcane.lysense.configuration.serialization.SemVerDeserializer;
import com.norcane.lysense.configuration.serialization.VariablesDeserializer;
import com.norcane.lysense.configuration.yaml.BaseVersionWrapper;
import com.norcane.lysense.configuration.yaml.YamlConfiguration;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.meta.RuntimeInfo;
import com.norcane.lysense.meta.SemVer;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.lysense.template.Variables;
import com.norcane.toolkit.state.Memoized;
import com.norcane.toolkit.state.Stateful;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.Reader;
import java.util.Set;

@ApplicationScoped
public class ConfigurationManager implements Stateful {

    private final Properties properties;
    private final ResourceLoader resourceLoader;
    private final RuntimeInfo runtimeInfo;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final Memoized<ConfigurationRef> configurationRef = Memoized.bindTo(this);

    @Inject
    public ConfigurationManager(Properties properties,
                                ResourceLoader resourceLoader,
                                RuntimeInfo runtimeInfo,
                                Validator validator) {

        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.runtimeInfo = runtimeInfo;
        this.validator = validator;

        this.objectMapper = yamlObjectMapper();
    }

    @Produces
    @ApplicationScoped
    public Configuration configuration() {
        return configurationRef().configuration();
    }

    public ConfigurationRef configurationRef() {
        return configurationRef.computeIfAbsent(() -> {
            final Resource defaultConfigurationResource = resourceLoader.resource(properties.defaultConfiguration());
            final Resource userConfigurationResource = switch (findConfigurationResource()) {
                case ConfigurationLookup.Found(var resource) -> resource;
                case ConfigurationLookup.NotFound(var uri) -> throw new NoConfigurationFoundException(uri);
            };

            verifyCompatibleBaseVersion(userConfigurationResource);
            return loadUserConfiguration(defaultConfigurationResource, userConfigurationResource);
        });
    }

    /**
     * Finds user configuration resource (if present).
     *
     * @return {@link ConfigurationLookup} instance
     */
    public ConfigurationLookup findConfigurationResource() {
        try {
            return new ConfigurationLookup.Found(resourceLoader.resource(runtimeInfo.userConfigurationPath().toString()));
        } catch (ResourceNotFoundException e) {
            return new ConfigurationLookup.NotFound(e.location());
        }
    }

    private void verifyCompatibleBaseVersion(Resource resource) {
        try (final Reader reader = resource.reader()) {
            final BaseVersionWrapper wrapper = objectMapper.readValue(reader, BaseVersionWrapper.class);

            if (wrapper.baseVersion() == null) {
                throw new MissingBaseVersionException(resource);
            }

            final SemVer minBaseVersion = properties.minBaseVersion();
            if (wrapper.baseVersion().isLowerThan(minBaseVersion)) {
                throw new IncompatibleConfigurationException(minBaseVersion, wrapper.baseVersion());
            }
        } catch (ApplicationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ConfigurationParseException(resource, t);
        }
    }

    private ConfigurationRef loadUserConfiguration(Resource defaultConfigurationResource, Resource userConfigurationResource) {
        try (final Reader defaultConfigReader = defaultConfigurationResource.reader();
             final Reader userConfigReader = userConfigurationResource.reader()) {

            // merge default and user configuration
            final YamlConfiguration defaultConfiguration = objectMapper.readValue(defaultConfigReader, YamlConfiguration.class);
            final YamlConfiguration mergedConfiguration =
                    objectMapper.readerForUpdating(defaultConfiguration).readValue(userConfigReader, YamlConfiguration.class);

            // validate final configuration
            final Set<ConstraintViolation<Configuration>> violations = validator.validate(mergedConfiguration);
            if (!violations.isEmpty()) {
                throw new InvalidConfigurationException(violations);
            }

            return new ConfigurationRef(mergedConfiguration, userConfigurationResource);
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationParseException(userConfigurationResource, e);
        }
    }

    private ObjectMapper yamlObjectMapper() {
        final SimpleModule module = new SimpleModule()
                .addDeserializer(HeaderStyle.class, LowerCaseDashSeparatedEnumDeserializer.forEnum(HeaderStyle.class))
                .addDeserializer(RunMode.class, LowerCaseDashSeparatedEnumDeserializer.forEnum(RunMode.class))
                .addDeserializer(SemVer.class, new SemVerDeserializer())
                .addDeserializer(Variables.class, new VariablesDeserializer());

        return new ObjectMapper(new YAMLFactory())
                .registerModule(module)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @ConfigMapping(prefix = "lysense.configuration")
    public interface Properties {

        @WithName("default")
        String defaultConfiguration();

        SemVer minBaseVersion();
    }
}
