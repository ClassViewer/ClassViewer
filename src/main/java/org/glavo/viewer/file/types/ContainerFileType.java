package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FileType;

public abstract class ContainerFileType extends FileType {

    protected ContainerFileType(String name) {
        super(name);
    }

    protected ContainerFileType(String name, Image image) {
        super(name, image);
    }


}
