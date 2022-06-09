package org.glavo.viewer.file.types.zip;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import kala.compress.utils.IOUtils;
import kala.compress.utils.SeekableInMemoryByteChannel;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.OldFilePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;

public class ArchiveFileHandle extends FileHandle {
    private final ZipArchiveEntry entry;

    public ArchiveFileHandle(ArchiveContainer container, OldFilePath path, ZipArchiveEntry entry) {
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
        ZipArchiveReader reader = ((ArchiveContainer) getContainer()).getReader();

        byte[] res = new byte[(int) entry.getSize()];
        try (InputStream input = reader.getInputStream(entry)) {
            int n = IOUtils.readFully(input, res);

            if (n != entry.getSize() || input.read() != -1) {
                throw new AssertionError();
            }
        }
        // TODO: open large file?
        return new SeekableInMemoryByteChannel(res);
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedEncodingException(); // TODO
    }
}
