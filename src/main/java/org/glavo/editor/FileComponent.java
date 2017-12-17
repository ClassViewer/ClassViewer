package org.glavo.editor;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
@Setter
public abstract class FileComponent extends TreeItem<FileComponent> implements Iterable<FileComponent> {
    protected int offset;
    protected int length;
    protected String name;
    protected String desc;

    protected boolean hasInit = false;

    @Override
    public ObservableList<TreeItem<FileComponent>> getChildren() {
        if (hasInit) {
            return super.getChildren();
        }

        for (val fc : this) {
            super.getChildren().add(fc);
        }

        return super.getChildren();
    }


    @Override
    public final String toString() {

        if (name != null && desc != null) {
            return name + ": " + desc;
        }
        if (name != null) {
            return name;
        }
        if (desc != null) {
            return desc;
        }

        return getClass().getSimpleName();
    }
}
