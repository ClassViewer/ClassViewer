package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.handles.PhysicalFileHandle;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.NavigableSet;
import java.util.TreeSet;

public class FolderContainer extends Container {
    private NavigableSet<FilePath> files;
    private final Path folder;

    public FolderContainer(FileHandle handle, Path folder) {
        super(handle);
        this.folder = folder;
    }

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
                    fs.add(new FilePath(file.toString(), false, getPath()));
                    return FileVisitResult.CONTINUE;
                }
            });

            return files = fs;
        }
    }

    @Override
    protected FileHandle openFileImpl(FilePath path) throws IOException {
        return new PhysicalFileHandle(path, Paths.get(path.normalize().getPath()));
    }
}
