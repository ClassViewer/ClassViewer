package org.glavo.viewer.file.stubs;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

public final class FolderStub extends FileStub {
    public FolderStub(Container container, FilePath path) {
        super(container, path);
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isReadonly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel openChannel() throws IOException {
        throw new UnsupportedOperationException("FolderStub");
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedOperationException("FolderStub");
    }
}
