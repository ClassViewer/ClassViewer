package org.glavo.viewer.file.types.folder;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.roots.local.LocalRootContainer;
import org.glavo.viewer.file.roots.local.LocalFileHandle;
import org.glavo.viewer.util.ArrayUtils;
import org.glavo.viewer.util.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.NavigableSet;
import java.util.Set;

public class FolderContainer extends Container {
    public FolderContainer(FileHandle handle) {
        super(handle);
    }

    @Override
    public Set<FilePath> list(FilePath dir) {
        return this.getFileHandle().getContainer().list(dir);
    }

    @Override
    protected FileHandle openFileImpl(FilePath path) throws IOException {
        return new LocalFileHandle(path, Paths.get(path.getPath()));
    }
}
