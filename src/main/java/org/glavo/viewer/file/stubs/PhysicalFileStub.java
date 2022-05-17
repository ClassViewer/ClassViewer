package org.glavo.viewer.file.stubs;

import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.RootContainer;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PhysicalFileStub extends FileStub {
    private final Path file;

    public PhysicalFileStub(FilePath path) {
        this(path, Paths.get(path.getPath()));
    }

    public PhysicalFileStub(FilePath path, Path file) {
        super(RootContainer.CONTAINER, path);
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
