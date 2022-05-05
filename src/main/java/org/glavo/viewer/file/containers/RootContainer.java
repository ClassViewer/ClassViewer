package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FilePath;

import java.util.NavigableSet;

public class RootContainer extends Container {
    public static final RootContainer CONTAINER = new RootContainer();

    private RootContainer() {
        super(null);
        this.increment(); // should not be closed
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() {
        throw new UnsupportedOperationException("RootContainer does not support indexing all files");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("RootContainer should not be closed");
    }
}