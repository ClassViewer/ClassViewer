package org.glavo.editor;

import javafx.scene.control.TreeItem;
import lombok.val;

public abstract class FileData<T extends FileComponent> extends TreeItem<FileComponent> implements Iterable<FileComponent> {
    {
        val list = getChildren();
        for (val it : this) {
            getChildren().add(it);
        }
    }
}
