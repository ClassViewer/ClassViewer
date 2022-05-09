package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FileType;

public abstract class CustomFileType extends FileType {
    protected CustomFileType(String name) {
        super(name);
    }

    protected CustomFileType(String name, Image image) {
        super(name, image);
    }
}
