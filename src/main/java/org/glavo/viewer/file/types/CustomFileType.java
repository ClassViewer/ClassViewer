package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.ui.FileTab;

public abstract class CustomFileType extends FileType {
    protected CustomFileType(String name) {
        super(name);
    }

    protected CustomFileType(String name, Image image) {
        super(name, image);
    }

    public FileTab openTab(FileHandle stub) {
        throw new UnsupportedOperationException("CustomFileType::openTab");
    }
}
