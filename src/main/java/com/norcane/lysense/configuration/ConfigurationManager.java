package com.norcane.lysense.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.norcane.lysense.configuration.domain.BaseVersionWrapper;
import com.norcane.lysense.configuration.domain.Configuration;
import com.norcane.lysense.configuration.exception.ConfigurationParseException;
import com.norcane.lysense.configuration.exception.IncompatibleConfigurationException;
import com.norcane.lysense.configuration.exception.InvalidConfigurationException;
import com.norcane.lysense.configuration.exception.MissingBaseVersionException;
import com.norcane.lysense.configuration.serialization.SemVerDeserializer;
import com.norcane.lysense.exception.ApplicationException;
import com.norcane.lysense.meta.RuntimeInfo;
import com.norcane.lysense.meta.SemVer;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.loader.ResourceLoader;
import com.norcane.toolkit.state.Memoized;
import com.norcane.toolkit.state.Stateful;

import java.io.Reader;
import java.util.Set;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ApplicationScoped
public class ConfigurationManager implements Stateful {

    private final Properties properties;
    private final ResourceLoader resourceLoader;
    private final RuntimeInfo runtimeInfo;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final Memoized<Configuration> configuration = Memoized.bindTo(this);

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

    public Configuration configuration() {
        return configuration.computeIfAbsent(() -> {
            final Resource defaultConfigurationResource = resourceLoader.resource(properties.defaultConfiguration());
            final Resource userConfigurationResource = resourceLoader.resource(runtimeInfo.userConfigurationPath());

            verifyCompatibleBaseVersion(userConfigurationResource);
            return loadUserConfiguration(defaultConfigurationResource, userConfigurationResource);
        });
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

    private Configuration loadUserConfiguration(Resource defaultConfigurationResource, Resource userConfigurationResource) {
        try (final Reader defaultConfigReader = defaultConfigurationResource.reader();
             final Reader userConfigReader = userConfigurationResource.reader()) {

            // merge default and user configuration
            final JsonNode defaultConfiguration = objectMapper.readValue(defaultConfigReader, JsonNode.class);
            final JsonNode userConfiguration = objectMapper.readerForUpdating(defaultConfiguration).readValue(userConfigReader, JsonNode.class);
            final Configuration mergedConfiguration = objectMapper.treeToValue(userConfiguration, Configuration.class);

            // validate final configuration
            final Set<ConstraintViolation<Configuration>> violations = validator.validate(mergedConfiguration);
            if (!violations.isEmpty()) {
                throw new InvalidConfigurationException(violations);
            }

            return mergedConfiguration;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationParseException(userConfigurationResource, e);
        }
    }

    private ObjectMapper yamlObjectMapper() {
        final SimpleModule module = new SimpleModule()
            .addDeserializer(SemVer.class, new SemVerDeserializer());

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
