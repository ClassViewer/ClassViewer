package org.glavo.viewer.file.stubs;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

public final class FolderStubs extends FileStubs {
    public FolderStubs(Container container, FilePath path) {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedOperationException();
    }
}
