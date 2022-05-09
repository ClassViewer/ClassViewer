package org.glavo.viewer.file;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class FileHandle implements Closeable {

    private final Container container;
    private final FilePath path;

    private final AtomicInteger counter = new AtomicInteger();

    protected FileHandle(Container container, FilePath path) {
        this.container = container;
        this.path = path;

        use();
    }

    public FileHandle use() {
        counter.getAndIncrement();
        return this;
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

    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedOperationException();
    }

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

    protected void closeImpl() throws Exception {
    }

    public final void close() {
        if (counter.decrementAndGet() == 0) {
            LOGGER.info("Release handle " + this);

            synchronized (container) {
                FileHandle h;
                if ((h = container.fileHandles.remove(getPath())) != this) {
                    System.out.println(">>> " + container.fileHandles);
                    throw new AssertionError(String.format("expected=%s, actual=%s", this, h));
                }

                try {
                    this.closeImpl();
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to close " + this, e);
                }

                container.checkStatus();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s[path=%s, container=%s]", this.getClass().getSimpleName(), this.getPath(), this.getContainer());
    }
}
