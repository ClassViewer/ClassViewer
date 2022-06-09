package org.glavo.viewer.file.root.local;

import org.glavo.viewer.file.*;
import org.glavo.viewer.file.root.RootContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.glavo.viewer.util.Logging.LOGGER;

public class LocalContainer extends RootContainer {
    public static final LocalContainer CONTAINER = new LocalContainer();
    private static final ContainerHandle ignored = new ContainerHandle(CONTAINER); // should not be closed

    private LocalContainer() {
        super(null);
    }

    @Override
    protected FileHandle openFileImpl(LocalFilePath path) throws IOException {
        assert path.isLocalFile();

        return new LocalFileHandle(path);
    }

    @Override
    public Set<LocalFilePath> list(LocalFilePath dir) {
        assert dir.isDefaultFileSystemPath();
        assert dir.isDirectory();

        try (var stream = Files.list(Paths.get(dir.toString()))) {
            return stream.map(it -> LocalFilePath.ofJavaPath(it, Files.isDirectory(it)))
                    .collect(Collectors.toSet());
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to get file list", e);
            return Collections.emptySet();
        }
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("LocalContainer should not be closed");
    }
}
