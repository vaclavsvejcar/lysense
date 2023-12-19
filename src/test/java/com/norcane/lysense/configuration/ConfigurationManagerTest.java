package com.norcane.lysense.configuration;

import com.norcane.lysense.configuration.api.Configuration;
import com.norcane.lysense.configuration.api.RunMode;
import com.norcane.lysense.configuration.exception.IncompatibleConfigurationException;
import com.norcane.lysense.meta.RuntimeInfo;
import com.norcane.lysense.meta.SemVer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@QuarkusTest
class ConfigurationManagerTest {

    static final String DEFAULT_CONFIGURATION_PATH = "classpath:/configuration/test-default-configuration.yaml";
    static final String USER_CONFIGURATION_PATH = "classpath:/configuration/test-user-configuration.yaml";

    @Inject
    ConfigurationManager configurationManager;

    @InjectMock
    ConfigurationManager.Properties properties;

    @InjectMock
    RuntimeInfo runtimeInfo;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(runtimeInfo);
        configurationManager.resetState();
    }

    @Test
    void configuration() {

        // -- mocks
        when(properties.defaultConfiguration()).thenReturn(DEFAULT_CONFIGURATION_PATH);
        when(properties.minBaseVersion()).thenReturn(SemVer.from("1.1.1"));
        when(runtimeInfo.userConfigurationPath()).thenReturn(USER_CONFIGURATION_PATH);

        final Configuration configuration = configurationManager.configuration();

        // -- verify
        verify(properties).defaultConfiguration();
        verify(properties).minBaseVersion();
        verify(runtimeInfo).userConfigurationPath();

        // -- assertions
        assertEquals(SemVer.from("1.2.3"), configuration.baseVersion());
        assertEquals(RunMode.UPDATE, configuration.runMode());
        assertEquals(List.of("path/to/templates"), configuration.templates());
        assertEquals(0, configuration.headerConfigs().get("java").headerSpacing().blankLinesAfter());
        assertEquals(2, configuration.headerConfigs().get("java").headerSpacing().blankLinesBefore());
        assertEquals(2, configuration.templateVariables().size());
    }

    @Test
    void configuration_incompatibleBaseVersion() {

        // -- mocks
        when(properties.defaultConfiguration()).thenReturn(DEFAULT_CONFIGURATION_PATH);
        when(properties.minBaseVersion()).thenReturn(SemVer.from("999.999.999"));
        when(runtimeInfo.userConfigurationPath()).thenReturn(USER_CONFIGURATION_PATH);

        assertThrows(IncompatibleConfigurationException.class, configurationManager::configuration);

        // -- verify
        verify(properties).defaultConfiguration();
        verify(properties).minBaseVersion();
        verify(runtimeInfo).userConfigurationPath();
    }

    @Produces
    @ApplicationScoped
    @Mock
    ConfigurationManager.Properties properties() {
        return mock(ConfigurationManager.Properties.class);
    }
}