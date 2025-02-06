/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer;

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
        return (List) getChildren();
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
    public final FileComponent get(String name) {
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
