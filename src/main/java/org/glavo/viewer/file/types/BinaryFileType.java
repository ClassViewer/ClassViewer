package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.Images;

public class BinaryFileType extends FileType {
    public BinaryFileType() {
        super("binary", Images.file);
    }

    protected BinaryFileType(String name) {
        super(name);
    }

    protected BinaryFileType(String name, Image image) {
        super(name, image);
    }
}
