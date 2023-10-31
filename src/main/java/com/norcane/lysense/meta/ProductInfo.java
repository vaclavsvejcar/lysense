package com.norcane.lysense.meta;

public final class ProductInfo {

    private ProductInfo() {
        // utility class - hence the private constructor
        throw new IllegalStateException("This class is not meant to be instantiated");
    }

    public static final String ERROR_CODE_PREFIX = "LSN";
}
