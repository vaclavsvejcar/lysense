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

import static com.norcane.lysense.source.LanguageId.languageId;
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
        assertEquals(0, configuration.headerConfigs().get(languageId("java")).headerSpacing().blankLinesAfter());
        assertEquals(2, configuration.headerConfigs().get(languageId("java")).headerSpacing().blankLinesBefore());
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
