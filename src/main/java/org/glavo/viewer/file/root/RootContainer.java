package org.glavo.viewer.file.root;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.util.NavigableSet;

public abstract class RootContainer extends Container {
    protected RootContainer(FileHandle handle) {
        super(handle);
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() throws Exception {
        throw new UnsupportedOperationException("RootContainer does not support indexing all files");
    }
}
