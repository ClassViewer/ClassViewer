package org.glavo.viewer.file.roots.local;

import org.glavo.viewer.file.*;
import org.glavo.viewer.file.RootContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class LocalRootContainer extends RootContainer {
    public static final LocalRootContainer CONTAINER = new LocalRootContainer();
    private static final ContainerHandle ignored = new ContainerHandle(CONTAINER); // should not be closed

    private LocalRootContainer() {
        super(null);
    }

    @Override
    protected FileHandle openFileImpl(FilePath path) throws IOException {
        assert path.isLocalFile();

        LocalFileHandle handle = new LocalFileHandle(path);
        if (!handle.exists()) {
            throw new NoSuchFileException(handle.getFile().toString());
        }

        return handle;
    }

    @Override
    public Set<FilePath> list(FilePath dir) throws IOException {
        assert dir.isLocalFile();
        assert dir.isDirectory();


        try (var stream = Files.list(Paths.get(dir.toString()))) {
            return stream.map(FilePath::ofJavaPath)
                    .collect(Collectors.toCollection(TreeSet::new));
        }
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("LocalContainer should not be closed");
    }
}
