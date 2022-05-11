package org.glavo.viewer.file.stubs;

import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.RootContainer;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PhysicalFileStubs extends FileStubs {
    private final Path file;

    public PhysicalFileStubs(FilePath path) {
        this(path, Paths.get(path.getPath()));
    }

    public PhysicalFileStubs(FilePath path, Path file) {
        super(RootContainer.CONTAINER, path);
        this.file = file;

        assert path.getParent() == null;
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
