package org.glavo.viewer.path;

public class FilePath {
    private final String path;
    private final FileType type;

    private final FilePath parent;

    public FilePath(String path, FileType type) {
        this(path, type, null);
    }

    public FilePath(String path, FileType type, FilePath parent) {
        this.path = path;
        this.type = type;
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public FileType getType() {
        return type;
    }

    public FilePath getParent() {
        return parent;
    }


}
