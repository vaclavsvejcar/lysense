package com.norcane.lysense.meta;

public final class ProductInfo {

    private ProductInfo() {
        // utility class - hence the private constructor
        throw new IllegalStateException("This class is not meant to be instantiated");
    }

    public static final String NAME = BuildInfo.NAME;
    public static final String DESCRIPTION = BuildInfo.DESCRIPTION;
    public static final SemVer VERSION = SemVer.from(BuildInfo.VERSION);

    public static final String ERROR_CODE_PREFIX = "LSN";
    public static final String USER_CONFIGURATION_FILE = ".lysense.yaml";
}
