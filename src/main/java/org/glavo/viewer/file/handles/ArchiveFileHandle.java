package org.glavo.viewer.file.handles;

import kala.compress.archivers.zip.ZipArchiveEntry;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

public class ArchiveFileHandle extends FileHandle {
    private final ZipArchiveEntry entry;

    protected ArchiveFileHandle(Container container, FilePath path, ZipArchiveEntry entry) {
        super(container, path);
        this.entry = entry;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isReadonly() {
        return true;
    }

    @Override
    public SeekableByteChannel openChannel() throws IOException {

        return null;
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        return null;
    }
}
