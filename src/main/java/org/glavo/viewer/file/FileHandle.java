package org.glavo.viewer.file;

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.ForceCloseable;
import org.glavo.viewer.util.SilentlyCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class FileHandle implements SilentlyCloseable, ForceCloseable {

    private final Container container;
    private final OldFilePath path;
    private CheckedRunnable<?> onForceClose;

    protected FileHandle(Container container, OldFilePath path) {
        this.container = container;
        this.path = path;
    }

    public synchronized void setOnForceClose(CheckedRunnable<?> onForceClose) {
        this.onForceClose = onForceClose;
    }

    public OldFilePath getPath() {
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
            return in.readAllBytes();
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

    private boolean closed = false;

    private synchronized void close(boolean force) {
        if (closed) {
            return;
        }
        closed = true;

        synchronized (getContainer()) {
            LOGGER.info("Close handle " + this);

            if (force && onForceClose != null) {
                try {
                    onForceClose.runChecked();
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to force close " + this, e);
                }
            }

            try {
                this.closeImpl();
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "Failed to close " + this, e);
            }

            FileHandle h;
            if ((h = getContainer().handles.remove(getPath())) != this) {
                throw new AssertionError(String.format("expected=%s, actual=%s", this, h));
            }
            container.checkStatus();
        }
    }

    @Override
    public final synchronized void close() {
        close(false);
    }

    @Override
    public void forceClose() {
        close(true);
    }

    @Override
    public String toString() {
        return String.format("%s[path=%s, container=%s]", this.getClass().getSimpleName(), this.getPath(), this.getContainer());
    }
}
