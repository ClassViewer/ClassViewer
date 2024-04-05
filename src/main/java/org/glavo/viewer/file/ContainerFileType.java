package org.glavo.viewer.file;

import javafx.scene.image.Image;

public abstract non-sealed class ContainerFileType extends FileType {

    protected ContainerFileType(String name) {
        super(name);
    }

    protected ContainerFileType(String name, Image image) {
        super(name, image);
    }

    public abstract Container openContainerImpl(FileHandle handle) throws Throwable;
}
