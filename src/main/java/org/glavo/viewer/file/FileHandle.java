package org.glavo.viewer.file;

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.ForceCloseable;
import org.glavo.viewer.util.SilentlyCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class FileHandle implements SilentlyCloseable, ForceCloseable {

    private final FileStub stub;
    private CheckedRunnable<?> onForceClose;

    protected FileHandle(FileStub stub) {
        this.stub = stub;
        synchronized (stub) {
            stub.handles.add(this);
        }
    }

    public synchronized void setOnForceClose(CheckedRunnable<?> onForceClose) {
        this.onForceClose = onForceClose;
    }

    public FileStub getStub() {
        return stub;
    }

    public FilePath getPath() {
        return getStub().getPath();
    }

    public Container getContainer() {
        return stub.getContainer();
    }

    public boolean exists() {
        return stub.exists();
    }

    public boolean isReadonly() {
        return stub.isReadonly();
    }

    public SeekableByteChannel openChannel() throws IOException {
        return stub.openChannel();
    }

    public SeekableByteChannel openWritableChannel() throws IOException {
        return stub.openWritableChannel();
    }

    public InputStream openInputStream() throws IOException {
        return stub.openInputStream();
    }

    public byte[] readAllBytes() throws IOException {
        return stub.readAllBytes();
    }

    public OutputStream openOutputStream() throws IOException {
        return stub.openOutputStream();
    }

    public void write(byte[] bytes) throws IOException {
        stub.write(bytes);
    }

    protected synchronized void closeImpl() throws Throwable {
        if (closed) {
            return;
        }
        closed = true;

        LOGGER.info("Force close handle " + this);
        try {
            if (onForceClose != null) {
                onForceClose.runChecked();
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to close " + this, e);
        }

        synchronized (stub) {
            stub.handles.remove(this);
            stub.checkStatus();
        }
    }

    private boolean closed = false;

    private synchronized void close(boolean force) {
        if (closed) {
            return;
        }
        closed = true;

        synchronized (stub) {
            if (!stub.handles.remove(this)) {
                throw new AssertionError("handle=" + this);
            }
            if (force) {
                LOGGER.info("Force close handle " + this);
                try {
                    if (onForceClose != null) {
                        onForceClose.runChecked();
                    }
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to close " + this, e);
                }
            } else {
                LOGGER.info("Close handle " + this);
            }
            stub.checkStatus();
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
        return String.format("%s[stub=%s]", this.getClass().getSimpleName(), getStub());
    }
}
