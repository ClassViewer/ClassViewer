package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.ContainerHandle;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.stubs.PhysicalFileStubs;

import java.io.IOException;
import java.util.NavigableSet;

public class RootContainer extends Container {
    public static final RootContainer CONTAINER = new RootContainer();

    private RootContainer() {
        super(null);

        //noinspection resource
        new ContainerHandle(this); // should not be closed
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() {
        throw new UnsupportedOperationException("RootContainer does not support indexing all files");
    }

    @Override
    protected FileStubs openFileImpl(FilePath path) throws IOException {
        assert path.getParent() == null;

        return new PhysicalFileStubs(path);
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("RootContainer should not be closed");
    }
}
