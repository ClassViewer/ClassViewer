package org.glavo.viewer.file.roots.local;

import org.glavo.viewer.file.*;
import org.glavo.viewer.file.RootContainer;

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
    protected FileHandle openFileImpl(FilePath path) throws IOException {
        assert path.isLocalFile();

        return new LocalFileHandle(path);
    }

    @Override
    public Set<FilePath> list(FilePath dir) {
        assert dir.isLocalFile();
        assert dir.isDirectory();

        try (var stream = Files.list(Paths.get(dir.toString()))) {
            return stream.map(it -> FilePath.ofJavaPath(it, Files.isDirectory(it)))
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
