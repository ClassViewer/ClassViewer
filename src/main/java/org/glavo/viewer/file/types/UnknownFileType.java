package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.resources.Images;

public class UnknownFileType extends BinaryFileType {
    public static final Image image = Images.loadImage("fileTypes/file-unknown.png");

    public UnknownFileType(String name) {
        super(name, image);
    }
}
