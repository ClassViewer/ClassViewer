package org.glavo.viewer.file.stubs;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import kala.compress.utils.IOUtils;
import kala.compress.utils.SeekableInMemoryByteChannel;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.ArchiveContainer;
import org.glavo.viewer.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;

public class ArchiveFileStubs extends FileStubs {
    private final ZipArchiveEntry entry;

    public ArchiveFileStubs(ArchiveContainer container, FilePath path, ZipArchiveEntry entry) {
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
            ZipArchiveReader reader = ((ArchiveContainer) getContainer()).getReader();

            byte[] res = new byte[(int) entry.getSize()];
            try (InputStream input = reader.getInputStream(entry)) {
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
