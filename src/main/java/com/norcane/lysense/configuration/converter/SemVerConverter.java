package com.norcane.lysense.configuration.converter;

import com.norcane.lysense.meta.SemVer;

import org.eclipse.microprofile.config.spi.Converter;

public class SemVerConverter implements Converter<SemVer> {

    @Override
    public SemVer convert(String value) {
        return SemVer.from(value);
    }
}
