package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.util.NavigableMap;

public class RootContainer extends Container {
    public static final RootContainer CONTAINER = new RootContainer();

    private RootContainer() {
        super(null, null);
        this.increment(); // should not be closed
    }

    @Override
    public NavigableMap<FilePath, VirtualFile> resolveFiles() {
        throw new UnsupportedOperationException("RootContainer does not support indexing all resolveFiles");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("RootContainer should not be closed");
    }
}
