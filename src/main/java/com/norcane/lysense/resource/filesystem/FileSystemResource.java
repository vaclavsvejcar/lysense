package com.norcane.lysense.resource.filesystem;

import com.norcane.lysense.resource.AbstractResource;
import com.norcane.lysense.resource.Resource;
import com.norcane.lysense.resource.exception.CannotReadResourceException;
import com.norcane.lysense.resource.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemResource extends AbstractResource {

    public static final Resource.Scheme SCHEME = new Resource.Scheme("file");

    private final Path path;

    private FileSystemResource(Path path, URI uri) {
        super(com.google.common.io.Files.getNameWithoutExtension(path.toString()),
              com.google.common.io.Files.getFileExtension(path.toString()),
              uri);

        this.path = path;
    }

    public static FileSystemResource of(URI uri) {
        final Path path = Path.of(uri.getSchemeSpecificPart());

        if (!Files.isRegularFile(path)) {
            throw new ResourceNotFoundException(uri);
        }

        return new FileSystemResource(path, uri);
    }

    public static FileSystemResource of(String path) {
        return of(Path.of(path).toUri());
    }

    @Override
    public Reader reader() {
        try {
            return Files.newBufferedReader(path);
        } catch (IOException e) {
            throw new CannotReadResourceException(this, e);
        }
    }
}
