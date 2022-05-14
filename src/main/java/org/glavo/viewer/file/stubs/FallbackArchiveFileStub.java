package org.glavo.viewer.file.stubs;

import kala.compress.utils.IOUtils;
import kala.compress.utils.SeekableInMemoryByteChannel;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.containers.FallbackArchiveContainer;
import org.glavo.viewer.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FallbackArchiveFileStub extends FileStub {
    private final ZipEntry entry;

    public FallbackArchiveFileStub(FallbackArchiveContainer container, FilePath path, ZipEntry entry) {
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
        if (entry.getSize() <= FileUtils.SMALL_FILE_LIMIT) {
            ZipFile file = ((FallbackArchiveContainer) getContainer()).getZipFile();

            byte[] res = new byte[(int) entry.getSize()];
            try (InputStream input = file.getInputStream(entry)) {
                int n = IOUtils.readFully(input, res);

                if (n != entry.getSize() || input.read() != -1) {
                    throw new AssertionError();
                }
            }
            return new SeekableInMemoryByteChannel(res);
        } else {
            throw new UnsupportedEncodingException(); // TODO
        }
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedEncodingException(); // TODO
    }
}
