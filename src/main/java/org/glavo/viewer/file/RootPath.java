package org.glavo.viewer.file;

public abstract class RootPath extends FilePath {
    @Override
    public RootPath getRootPath() {
        return this;
    }
}
