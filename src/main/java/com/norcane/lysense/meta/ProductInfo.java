package com.norcane.lysense.meta;

public final class ProductInfo {

    private ProductInfo() {
        // utility class - hence the private constructor
        throw new IllegalStateException("This class is not meant to be instantiated");
    }

    public static final String NAME = BuildInfo.NAME;
    public static final String DESCRIPTION = BuildInfo.DESCRIPTION;
    public static final String VERSION_STRING = BuildInfo.VERSION;
    public static final SemVer VERSION = SemVer.from(VERSION_STRING);
    public static final String URL_HOMEPAGE = "https://github.com/vaclavsvejcar/lysense";
    public static final String URL_REPORT_BUG = "https://github.com/vaclavsvejcar/lysense/issues/new";


    public static final String ERROR_CODE_PREFIX = "LSN";
    public static final String USER_CONFIGURATION_FILE = "lysense.yaml";

    public static String productHeader() {
        return STR."Welcome to @|bold,magenta \{NAME} \{VERSION}|@ :: @|underline \{URL_HOMEPAGE}|@";
    }
}
