package org.glavo.viewer.file;

import org.glavo.viewer.util.ForceCloseable;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class FileStub implements ForceCloseable {

    private final Container container;
    private final FilePath path;

    final Set<FileHandle> handles = new HashSet<>();

    protected FileStub(Container container, FilePath path) {
        this.container = container;
        this.path = path;
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

    public synchronized final void checkStatus() {
        if (handles.isEmpty()) {
            forceClose();
        }
    }

    @Override
    public final synchronized void forceClose() {
        LOGGER.info("Release stub " + this);

        for (FileHandle handle : handles) {
            synchronized (handle) {
                LOGGER.info("Close handle " + handle);
                try {
                    handle.closeImpl();
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to close " + handle, e);
                }
            }
        }
        handles.clear();

        synchronized (container) {
            FileStub h;
            if ((h = container.fileStubs.remove(getPath())) != this) {
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

    @Override
    public String toString() {
        return String.format("%s[path=%s, container=%s]", this.getClass().getSimpleName(), this.getPath(), this.getContainer());
    }
}
