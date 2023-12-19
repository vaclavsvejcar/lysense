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

    Map<String, ? extends HeaderConfig> headerConfigs();

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
