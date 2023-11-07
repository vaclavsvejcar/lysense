package com.norcane.lysense.configuration;

import com.norcane.lysense.configuration.domain.Configuration;
import com.norcane.lysense.meta.RuntimeInfo;
import com.norcane.lysense.meta.SemVer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@QuarkusTest
class ConfigurationManagerTest {

    static final String USER_CONFIGURATION_PATH = "classpath:/configuration/test-user-configuration.yaml";

    @Inject
    ConfigurationManager configurationManager;

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
        when(runtimeInfo.userConfigurationPath()).thenReturn(USER_CONFIGURATION_PATH);

        final Configuration configuration = configurationManager.configuration();

        // -- verify
        verify(runtimeInfo).userConfigurationPath();

        // -- assertions
        assertEquals(SemVer.from("1.2.3"), configuration.baseVersion());
    }
}