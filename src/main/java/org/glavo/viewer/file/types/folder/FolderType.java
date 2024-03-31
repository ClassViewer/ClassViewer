package org.glavo.viewer.file.types.folder;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.resources.Images;

import java.io.IOException;

public final class FolderType extends ContainerFileType {
    public static final FolderType TYPE = new FolderType();

    private FolderType() {
        super("folder", Images.folder);
    }

    @Override
    public boolean check(FilePath path) {
        return false;
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws IOException {
        assert handle instanceof FolderHandle;
        assert handle.getPath().isDirectory();

        return new FolderContainer(handle);
    }
}
