package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.stubs.PhysicalFileStubs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.NavigableSet;
import java.util.TreeSet;

public class FolderContainer extends Container {
    private NavigableSet<FilePath> files;
    private final Path folder;

    public FolderContainer(FileStubs handle, Path folder) {
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
                    fs.add(FilePath.ofJavaPath(file));
                    return FileVisitResult.CONTINUE;
                }
            });

            return files = fs;
        }
    }

    @Override
    protected FileStubs openFileImpl(FilePath path) throws IOException {
        return new PhysicalFileStubs(path, Paths.get(path.getPath()));
    }
}
