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
package com.norcane.lysense.configuration.api;

import com.norcane.lysense.configuration.exception.HeaderConfigNotFoundException;
import com.norcane.lysense.meta.SemVer;
import com.norcane.lysense.source.LanguageId;
import com.norcane.lysense.template.Variables;

import java.util.List;
import java.util.Map;

/**
 * Configuration of the application.
 */
public interface Configuration {

    /**
     * Base version of the configuration, based on this it's possible to check whether current version of application is compatible with the configuration.
     *
     * @return base version of the configuration
     */
    SemVer baseVersion();

    /**
     * Mode of the <i>run command</i> of the application. Defines how should be existing license headers handled.
     *
     * @return run mode
     */
    RunMode runMode();

    /**
     * List of templates paths to be used for generating the license.
     *
     * @return list of template paths
     */
    List<String> templates();

    /**
     * List of source code paths to be used for adding, dropping or removing license headers.
     *
     * @return list of source code paths
     */
    List<String> sources();

    /**
     * Map of header configurations, where key is language ID and value is header configuration.
     *
     * @return map of header configurations
     */
    Map<LanguageId, ? extends HeaderConfig> headerConfigs();

    Variables templateVariables();

    /**
     * Returns header configuration for given language ID.
     *
     * @param languageId language ID
     * @return header configuration
     * @throws HeaderConfigNotFoundException if header configuration for given language ID is not found
     */
    default HeaderConfig headerConfigOrFail(LanguageId languageId) {
        final HeaderConfig headerConfig = headerConfigs().get(languageId);

        if (headerConfig == null) {
            throw new HeaderConfigNotFoundException(languageId);
        }

        return headerConfig;
    }
}
