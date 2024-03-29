package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FileType;

public abstract non-sealed class ContainerFileType extends FileType {

    protected ContainerFileType(String name) {
        super(name);
    }

    protected ContainerFileType(String name, Image image) {
        super(name, image);
    }

    public abstract Container openContainerImpl(FileHandle handle) throws Throwable;
}
