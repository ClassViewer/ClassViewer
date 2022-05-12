package org.glavo.viewer.file.stubs;

import kala.compress.utils.SeekableInMemoryByteChannel;
import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.containers.JImageContainer;
import org.glavo.viewer.util.FileUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;

public class JImageFileStub extends FileStub {
    private final ImageReader reader;
    private final ImageReader.Node node;

    public JImageFileStub(JImageContainer container, FilePath path, ImageReader reader, ImageReader.Node node) {
        super(container, path);
        this.reader = reader;
        this.node = node;
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
        if (node.size() <= FileUtils.SMALL_FILE_LIMIT) {
            return new SeekableInMemoryByteChannel(reader.getResource(node));
        } else {
            throw new UnsupportedEncodingException(); // TODO
        }
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedEncodingException(); // TODO
    }
}
