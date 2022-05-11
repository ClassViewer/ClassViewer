package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.FolderContainer;
import org.glavo.viewer.file.stubs.FolderStubs;
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
    public Container openContainerImpl(FileStubs handle) throws IOException {
        assert handle instanceof FolderStubs;
        assert handle.getPath().isDirectory();
        assert handle.getPath().getParent() == null;


        Path p = Paths.get(handle.getPath().toString());

        return new FolderContainer(handle, p);
    }
}
