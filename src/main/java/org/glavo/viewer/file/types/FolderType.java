package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.FolderContainer;
import org.glavo.viewer.file.stubs.FolderStub;
import org.glavo.viewer.resources.Images;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        assert handle.getStub() instanceof FolderStub;
        assert handle.getPath().isDirectory();
        assert handle.getPath().getParent() == null;


        Path p = Paths.get(handle.getPath().toString());

        return new FolderContainer(handle, p);
    }
}
