package org.glavo.viewer.file2.roots.local;

import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.roots.local.LocalRootContainer;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LocalFileHandle extends FileHandle {
    private final Path file;

    public LocalFileHandle(FilePath path) {
        this(path, Paths.get(path.getPath()));
    }

    public LocalFileHandle(FilePath path, Path file) {
        super(LocalRootContainer.CONTAINER, path);
        this.file = file;

        assert path.isLocalFile();
    }

    @Override
    public boolean exists() {
        return Files.exists(file);
    }

    public Path getFile() {
        return file;
    }

    @Override
    public boolean isReadonly() {
        return exists() && !Files.isWritable(file);
    }

    @Override
    public SeekableByteChannel openChannel() throws IOException {
        return FileChannel.open(file);
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        return FileChannel.open(file, StandardOpenOption.WRITE);
    }
}
