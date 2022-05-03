package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.resources.Images;

import java.io.IOException;

public final class FolderType extends ContainerFileType {
    public FolderType() {
        super("folder", Images.folder);
    }

    @Override
    public boolean check(FilePath path) {
        return false;
    }

    @Override
    public Container open(FilePath path) throws IOException {
        throw new AssertionError();
    }
}
