package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.handles.PhysicalFileHandle;

import java.io.IOException;
import java.nio.file.Paths;
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
    protected FileHandle openFileImpl(FilePath path) throws IOException {
        assert path.getParent() == null;

        return new PhysicalFileHandle(path);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("RootContainer should not be closed");
    }
}
