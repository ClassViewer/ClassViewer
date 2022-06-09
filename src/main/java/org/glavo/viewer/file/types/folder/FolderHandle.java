package org.glavo.viewer.file.types.folder;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.LocalFilePath;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

public final class FolderHandle extends FileHandle {
    public FolderHandle(Container container, LocalFilePath path) {
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
        throw new UnsupportedOperationException("FolderHandle");
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedOperationException("FolderHandle");
    }
}
