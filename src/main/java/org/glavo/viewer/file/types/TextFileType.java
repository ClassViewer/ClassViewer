package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileType;

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

    @Override
    public boolean check(FilePath path) {
        throw new UnsupportedOperationException();
    }
}
