package org.glavo.viewer.file.types;

import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.Images;

public final class FolderType extends FileType implements ContainerFileType {
    public FolderType() {
        super("folder", Images.folder);
    }
}
