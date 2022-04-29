package org.glavo.viewer.file.types;

import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.Images;

public class JImageFileType extends FileType implements ContainerFileType {
    public JImageFileType() {
        super("jimage", Images.loadImage("fileTypes/file-archive.png"));
    }
}
