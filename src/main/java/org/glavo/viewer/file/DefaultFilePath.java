package org.glavo.viewer.file;

public final class DefaultFilePath extends FilePath {
    public DefaultFilePath(String[] pathElements, boolean isDirectory, FilePath parent) {
        super(pathElements, isDirectory, parent);
    }

    private String str;

    @Override
    public String toString() {
        if (str == null) {
            str = getParent() + "/" + getPath();
        }

        return str;
    }
}
