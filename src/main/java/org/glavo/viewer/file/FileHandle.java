package org.glavo.viewer.file;

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.SilentlyCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class FileHandle implements SilentlyCloseable {

    private final FileStub stub;
    private CheckedRunnable<?> onClose;

    protected FileHandle(FileStub stub) {
        this.stub = stub;
        synchronized (stub) {
            stub.handles.add(this);
        }
    }

    public synchronized void setOnClose(CheckedRunnable<?> onClose) {
        this.onClose = onClose;
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
        if (onClose != null) {
            onClose.runChecked();
        }
    }

    @Override
    public final void close() {
        LOGGER.info("Close handle " + this);
        try {
            closeImpl();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to close " + this, e);
        }

        synchronized (stub) {
            stub.handles.remove(this);
            stub.checkStatus();
        }
    }

    @Override
    public String toString() {
        return String.format("%s[stub=%s]", this.getClass().getSimpleName(), getStub());
    }
}
