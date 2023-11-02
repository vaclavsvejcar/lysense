package com.norcane.toolkit.net;

import com.google.common.net.UrlEscapers;

import java.net.URI;

public final class URIs {

    private URIs() {
        // utility class - hence the private constructor
        throw new IllegalStateException();
    }

    public static URI create(String path) {
        return URI.create(escape(path));
    }

    public static String escape(String string) {
        return UrlEscapers.urlFragmentEscaper().escape(string);
    }
}
