package org.glavo.viewer.file;

public non-sealed abstract class RootPath extends FilePath {
    protected DefaultFilePath createTopPath(String[] pathElements, boolean isDirectory) {
        return new DefaultFilePath(pathElements, isDirectory, this);
    }

    @Override
    public RootPath getRootPath() {
        return this;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && this.getClass() == obj.getClass()
                && this.toString().equals(obj.toString());
    }
}
