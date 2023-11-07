package com.norcane.lysense.meta;

import java.io.File;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RuntimeInfo {

    public String userConfigurationPath() {
        return System.getProperty("user.dir") + File.separator + ProductInfo.USER_CONFIGURATION_FILE;
    }
}
