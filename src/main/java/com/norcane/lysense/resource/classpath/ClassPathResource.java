package com.norcane.lysense.resource.classpath;

import com.google.common.base.MoreObjects;
import com.google.common.io.Files;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;
import com.norcane.toolkit.net.URIs;

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
        enforceScheme(uri, SCHEME);

        return new ClassPathResource(uri);
    }

    public static ClassPathResource of(String classPath) {
        return of(URIs.create(STR. "\{ SCHEME.value() }:\{ classPath }" ));
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(nonNull(getClass().getResourceAsStream(location.getSchemeSpecificPart())));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("location", location)
            .toString();
    }
}
