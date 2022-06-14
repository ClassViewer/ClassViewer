package org.glavo.viewer.file;

public abstract class RootPath extends FilePath {
    public RootPath() {
        super(null, true, null);
    }

    protected FilePath createPath(String[] pathElements, boolean isDirectory) {
        return new DefaultFilePath(pathElements, isDirectory, this);
    }

    protected int order() {
        return Integer.MAX_VALUE;
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
