package org.glavo.viewer.file.root.local;

import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.LocalFilePath;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LocalFileHandle extends FileHandle {
    private final Path file;

    public LocalFileHandle(LocalFilePath path) {
        this(path, Paths.get(path.getPath()));
    }

    public LocalFileHandle(LocalFilePath path, Path file) {
        super(LocalContainer.CONTAINER, path);
        this.file = file;

        assert path.getParent().isLocalFile();
    }

    @Override
    public boolean exists() {
        return Files.exists(file);
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
