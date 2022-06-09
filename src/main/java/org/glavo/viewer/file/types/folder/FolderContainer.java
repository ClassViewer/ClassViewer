package org.glavo.viewer.file.types.folder;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.OldFilePath;
import org.glavo.viewer.file.root.local.LocalContainer;
import org.glavo.viewer.file.root.local.LocalFileHandle;

import java.io.IOException;
import java.nio.file.*;
import java.util.NavigableSet;
import java.util.Set;

public class FolderContainer extends Container {
    private NavigableSet<OldFilePath> files;
    private final Path folder;

    public FolderContainer(FileHandle handle, Path folder) {
        super(handle);
        this.folder = folder;
    }

    /*
    @Override
    public NavigableSet<FilePath> resolveFiles() throws IOException {
        if (files != null) {
            return files;
        }

        synchronized (this) {
            if (files != null) {
                return files;
            }

            NavigableSet<FilePath> fs = new TreeSet<>();

            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fs.add(FilePath.ofJavaPath(file));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    LOGGER.log(Level.WARNING, "An exception occurred while resolve " + getFileHandle().getPath(), exc);
                    return FileVisitResult.CONTINUE;
                }
            });

            return files = fs;
        }
    }
     */

    @Override
    public Set<OldFilePath> list(OldFilePath dir) {
        return LocalContainer.CONTAINER.list(dir);
    }

    @Override
    protected FileHandle openFileImpl(OldFilePath path) throws IOException {
        return new LocalFileHandle(path, Paths.get(path.getPath()));
    }
}
