package org.glavo.viewer.file;

public class TopFilePath extends FilePath {
    private final String[] pathElements;
    private final boolean isDirectory;
    private final RootPath rootPath;
    private final String path;

    public TopFilePath(String[] pathElements, boolean isDirectory, RootPath rootPath) {
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
    public String toString() {
        if (str == null) {
            str = getRootPath() + "/" + getPath();
        }

        return str;
    }
}
