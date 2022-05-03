package org.glavo.viewer.file;

import java.io.IOException;
import java.util.NavigableSet;

public class RootContainer extends Container {
    public static final RootContainer CONTAINER = new RootContainer();

    private RootContainer() {
        super(null);
        this.increment(); // should not be closed
    }

    @Override
    public NavigableSet<VirtualFile> files() {
        throw new UnsupportedOperationException("RootContainer does not support indexing all files");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("RootContainer should not be closed");
    }
}
