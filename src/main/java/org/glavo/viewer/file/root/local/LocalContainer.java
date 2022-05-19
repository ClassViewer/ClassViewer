package org.glavo.viewer.file.root.local;

import org.glavo.viewer.file.*;
import org.glavo.viewer.file.root.RootContainer;

import java.io.IOException;

public class LocalContainer extends RootContainer {
    public static final LocalContainer CONTAINER = new LocalContainer();
    private static final ContainerHandle ignored = new ContainerHandle(CONTAINER); // should not be closed

    private LocalContainer() {
        super(null);
    }

    @Override
    protected FileHandle openFileImpl(FilePath path) throws IOException {
        assert path.isLocalFile();

        return new LocalFileHandle(path);
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("LocalContainer should not be closed");
    }
}
