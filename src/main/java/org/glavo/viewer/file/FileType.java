package org.glavo.viewer.file;

import javafx.scene.image.Image;
import org.glavo.viewer.resources.Images;

import java.util.Locale;

public enum FileType {
    FOLDER(true, Images.loadImage("folder.png")),
    ARCHIVE(true),
    JIMAGE(true, ARCHIVE.image),
    JAVA_CLASS,
    MANIFEST,
    PROPERTIES,
    TEXT,
    UNKNOWN(Images.loadImage("file.png"));

    private final boolean isContainer;
    private final Image image;

    FileType() {
        this(false);
    }

    FileType(boolean isContainer) {
        this.isContainer = isContainer;
        this.image = Images.loadImage("fileTypes/file-" + name().toLowerCase(Locale.ROOT).replace('_', '-') + ".png");
    }

    FileType(Image image) {
        this(false, image);
    }

    FileType(boolean isContainer, Image image) {
        this.isContainer = isContainer;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }
}
