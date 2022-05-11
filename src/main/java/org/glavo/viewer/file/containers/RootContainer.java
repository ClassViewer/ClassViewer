package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.ContainerHandle;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.stubs.PhysicalFileStub;

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
    protected FileStub openFileImpl(FilePath path) throws IOException {
        assert path.getParent() == null;

        return new PhysicalFileStub(path);
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("RootContainer should not be closed");
    }
}
