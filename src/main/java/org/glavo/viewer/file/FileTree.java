package org.glavo.viewer.file;

import javafx.scene.control.TreeItem;

public abstract class FileTree extends TreeItem<FileTree> {
    public FileTree() {
        this.setValue(this);
    }
}
