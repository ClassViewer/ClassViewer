package org.glavo.viewer.file;

import javafx.scene.control.TreeItem;

public final class FileTree extends TreeItem<String> {
    private final FileType type;
    private final FilePath path;

    private Status status;

    public FileTree(FileType type, FilePath path) {
        this.type = type;
        this.path = path;
    }

    public FileType getType() {
        return type;
    }

    public FilePath getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        DEFAULT,
        FAILED,
        LOADING
    }
}
