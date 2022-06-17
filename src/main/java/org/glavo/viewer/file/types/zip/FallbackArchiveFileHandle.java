package org.glavo.viewer.file.types.zip;

import kala.compress.utils.IOUtils;
import kala.compress.utils.SeekableInMemoryByteChannel;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FallbackArchiveFileHandle extends FileHandle {
    private final ZipEntry entry;

    public FallbackArchiveFileHandle(FallbackArchiveContainer container, FilePath path, ZipEntry entry) {
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
        ZipFile file = ((FallbackArchiveContainer) getContainer()).getZipFile();

        byte[] res = new byte[(int) entry.getSize()];
        try (InputStream input = file.getInputStream(entry)) {
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
