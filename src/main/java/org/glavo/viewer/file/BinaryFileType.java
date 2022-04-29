package org.glavo.viewer.file;

import javafx.scene.image.Image;

public class BinaryFileType extends FileType {
    public BinaryFileType() {
        super("binary");
    }

    protected BinaryFileType(String name) {
        super(name);
    }

    protected BinaryFileType(String name, Image image) {
        super(name, image);
    }
}
