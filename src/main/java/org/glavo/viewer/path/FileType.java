package org.glavo.viewer.path;

import javafx.scene.image.Image;
import org.glavo.viewer.resources.Images;

import java.util.Locale;

public enum FileType {
    JAVA_CLASS,
    ARCHIVE,
    TEXT,
    UNKNOWN;

    private final Image image = Images.loadImage("fileTypes/file-" + this.name().toLowerCase(Locale.ROOT).replace('_', '-') + ".png");

    public Image getImage() {
        return image;
    }
}
