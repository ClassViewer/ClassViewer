package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.Images;

public class UnknownFileType extends FileType {
    public static final Image image = Images.loadImage("fileTypes/file-unknown.png");

    public UnknownFileType(String name) {
        super(name, image);
    }
}
