package org.glavo.viewer.file;

import org.glavo.viewer.util.ReferenceCounter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class FileHandle extends ReferenceCounter {

    private final Container container;
    private final FilePath path;

    protected FileHandle(Container container, FilePath path) {
        this.container = container;
        this.path = path;
        this.increment();
    }

    public FilePath getPath() {
        return path;
    }

    public Container getContainer() {
        return container;
    }

    public abstract boolean exists();

    public abstract boolean isReadonly();

    public abstract SeekableByteChannel openChannel() throws IOException;

    public abstract SeekableByteChannel openWritableChannel() throws IOException;

    public InputStream openInputStream() throws IOException {
        return Channels.newInputStream(openChannel());
    }

    public byte[] readAllBytes() throws IOException {
        try (InputStream in = openInputStream()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();
        }
    }

    public OutputStream openOutputStream() throws IOException {
        return Channels.newOutputStream(openWritableChannel());
    }

    public void write(byte[] bytes) throws IOException {
        try (OutputStream out = openOutputStream()) {
            out.write(bytes);
        }
    }

    protected void close() throws Exception {
    }

    @Override
    protected final void onRelease() {
        LOGGER.info("Release handle " + this);

        try {
            this.close();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to close " + this, e);
        } finally {
            container.decrement();
        }
    }

    @Override
    public String toString() {
        return String.format("%s[path=%s, container=%s]", this.getClass().getSimpleName(), this.getPath(), this.getContainer());
    }
}
