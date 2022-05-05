package org.glavo.viewer.file;

import org.glavo.viewer.util.ReferenceCounter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container extends ReferenceCounter implements Closeable {

    private static final Map<FilePath, Container> containerMap = new ConcurrentHashMap<>();

    private final FileHandle handle;

    protected Container(FileHandle handle) {
        this.handle = handle;
    }

    public FilePath getPath() {
        return handle.getPath();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public abstract NavigableSet<FilePath> resolveFiles() throws Exception;

    public boolean isReadonly() {
        return true;
    }

    @Override
    protected final void onRelease() {
        LOGGER.info("Release container " + this);
        Container container = containerMap.remove(getPath());
        if (container != this) {
            throw new AssertionError("this is" + this + ", but container=" + container);
        }

        try {
            this.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close " + this, e);
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s[handle=%s]", this.getClass().getSimpleName(), getFileHandle());
    }
}
