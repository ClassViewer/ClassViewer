package org.glavo.viewer.file;

import javafx.scene.image.Image;

public class TextFileType extends FileType {

    public TextFileType() {
        super("text");
    }

    protected TextFileType(String name) {
        super(name);
    }

    protected TextFileType(String name, Image image) {
        super(name, image);
    }
}
