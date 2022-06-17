package org.glavo.viewer.file;

public class RootContainerNotOpenException extends RuntimeException {
    private final RootPath path;

    public RootContainerNotOpenException(RootPath path) {
        this.path = path;
    }

    public RootPath getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "path=" + path;
    }
}
