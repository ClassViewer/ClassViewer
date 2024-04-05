package org.glavo.viewer.file2.roots.local;

import org.glavo.viewer.file2.Container;
import org.glavo.viewer.file2.ContainerHandle;
import org.glavo.viewer.file2.FileHandle;
import org.glavo.viewer.file2.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class LocalRootContainer extends Container {
    public static final LocalRootContainer CONTAINER = new LocalRootContainer();
    private static final ContainerHandle ignored = new ContainerHandle(CONTAINER); // should not be closed

    private LocalRootContainer() {
        super(null);
    }

    @Override
    protected FileHandle openFileImpl(VirtualFile file) throws IOException, NoSuchFileException {
        return null;
    }

    @Override
    public List<VirtualFile> list(VirtualFile dir) throws Throwable {
        if (!(dir instanceof LocalFile localDir)) {
            throw new IllegalArgumentException(dir + " is not a LocalFile");
        }

        try (Stream<Path> stream = Files.list(localDir.getPath())) {
            return stream.<VirtualFile>map(LocalFile::new).toList();
        }
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("LocalContainer should not be closed");
    }
}
