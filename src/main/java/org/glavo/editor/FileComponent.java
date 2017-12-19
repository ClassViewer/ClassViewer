package org.glavo.editor;

import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * Base class for all file components.
 */
public abstract class FileComponent extends TreeItem<FileComponent> {

    private String name;
    private String desc; // description
    private int offset; // the position of this FileComponent in the file
    private int length; // how many bytes this FileComponent has

    public FileComponent() {
        setValue(this);
    }

    // Getters & Setters
    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getDesc() {
        return desc;
    }

    public final void setDesc(String desc) {
        this.desc = desc;
    }

    public final int getOffset() {
        return offset;
    }

    public final void setOffset(int offset) {
        this.offset = offset;
    }

    public final int getLength() {
        return length;
    }

    public final void setLength(int length) {
        this.length = length;
    }

    @SuppressWarnings("unchecked")
    public List<FileComponent> getComponents() {
        List<?> children = getChildren();
        return (List<FileComponent>) children;
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    /**
     * Find sub-component by name.
     *
     * @param name name of sub-component
     * @return value of sub-component
     */
    protected final FileComponent get(String name) {
        for (FileComponent c : getComponents()) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    protected final void add(String name, FileComponent subComponent) {
        if (name != null) {
            subComponent.setName(name);
        }
        getChildren().add(subComponent);
    }

    /**
     * The returned string will be displayed by BytesTreeItem.
     *
     * @return
     */
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
