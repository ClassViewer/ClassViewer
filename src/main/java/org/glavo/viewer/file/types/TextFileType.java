package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileType;

public class TextFileType extends FileType {
    public static final TextFileType TYPE = new TextFileType();

    protected TextFileType() {
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
        return path.getFileName().endsWith(".txt");
    }
}
