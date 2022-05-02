package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.resources.Images;

public class JImageFileType extends ContainerFileType {
    public JImageFileType() {
        super("jimage", Images.loadImage("fileTypes/file-archive.png"));
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().equals("modules");
    }
}
