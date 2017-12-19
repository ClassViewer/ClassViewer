package org.glavo.editor;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public abstract class FileComponent<T extends FileComponent<T>> extends TreeItem<T> implements Iterable<T> {
    @Getter
    @Setter
    protected int offset;

    @Getter
    @Setter
    protected int length;

    @Getter
    @Setter
    protected String name;

    @Getter
    @Setter
    protected String desc;

    protected boolean hasInit = false;

    public void update() {
        if (!hasInit) {
            for (val fc : this) {
                super.getChildren().add(fc);
            }
        }
    }

    @Override
    public ObservableList<TreeItem<T>> getChildren() {
        update();
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
