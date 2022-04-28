package org.glavo.viewer.path;

import javafx.scene.image.Image;
import org.glavo.viewer.resources.Images;

import java.util.Locale;

public enum FileType {
    FOLDER(Images.loadImage("folder.png")),
    ARCHIVE,
    JAVA_CLASS,
    MANIFEST,
    PROPERTIES,
    TEXT,
    UNKNOWN(Images.loadImage("file.png"));

    private final Image image;

    FileType() {
        this.image = Images.loadImage("fileTypes/file-" + name().toLowerCase(Locale.ROOT).replace('_', '-') + ".png");
    }

    FileType(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }
}
