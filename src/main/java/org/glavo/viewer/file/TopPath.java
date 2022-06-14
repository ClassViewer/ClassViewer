package org.glavo.viewer.file;

public abstract non-sealed class TopPath extends FilePath {
    private final String[] pathElements;
    private final boolean isDirectory;
    private final RootPath rootPath;
    private final String path;

    public TopPath(String[] pathElements, boolean isDirectory, RootPath rootPath) {
        this.pathElements = pathElements;
        this.isDirectory = isDirectory;
        this.rootPath = rootPath;

        this.path = String.join("/", pathElements);
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public RootPath getRootPath() {
        return rootPath;
    }

    public String getPath() {
        return path;
    }

    protected String str;

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.getPath().hashCode() + getRootPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TopPath that
                && this.getClass() == that.getClass()
                && getRootPath().equals(that.getRootPath())
                && getPath().equals(that.getPath());
    }

    @Override
    public String toString() {
        if (str == null) {
            str = getRootPath() + "/" + getPath();
        }

        return str;
    }
}
