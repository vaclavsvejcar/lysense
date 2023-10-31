package com.norcane.lysense.resource;

import java.io.Reader;
import java.net.URI;

public interface Resource {

    String name();

    String extension();

    URI location();

    LineSeparator lineSeparator();

    Reader reader();
}
