package com.norcane.lysense.configuration.api;

import com.norcane.lysense.meta.SemVer;

import java.util.List;

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
     * List of templates paths to be used for generating the license.
     *
     * @return list of template paths
     */
    List<String> templates();
}
