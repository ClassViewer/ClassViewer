package org.glavo.viewer.file;

import org.glavo.viewer.resources.Images;

public final class FolderType extends FileType implements ContainerFileType {
    public FolderType() {
        super("folder", Images.loadImage("folder.png"));
    }
}
