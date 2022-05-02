package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.resources.Images;

public final class FolderType extends ContainerFileType {
    public FolderType() {
        super("folder", Images.folder);
    }

    @Override
    public boolean check(FilePath path) {
        return false;
    }
}