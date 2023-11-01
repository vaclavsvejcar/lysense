package com.norcane.lysense.resource.classpath;

import com.google.common.io.Files;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import static com.norcane.toolkit.Prelude.nonNull;

public class ClassPathResource extends AbstractResource {

    static final Resource.Scheme SCHEME = new Resource.Scheme("classpath");

    private ClassPathResource(URI uri) {
        super(Files.getNameWithoutExtension(uri.getSchemeSpecificPart()), Files.getFileExtension(uri.getSchemeSpecificPart()), uri);
    }

    public static ClassPathResource of(URI uri) {
        nonNull(uri);
        enforceSchemeIfPresent(uri, SCHEME);

        return new ClassPathResource(uri);
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(nonNull(getClass().getResourceAsStream(location.getSchemeSpecificPart())));
    }
}
