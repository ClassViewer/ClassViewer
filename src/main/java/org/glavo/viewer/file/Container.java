package org.glavo.viewer.file;

import org.glavo.viewer.util.ReferenceCounter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container extends ReferenceCounter {

    private static final Map<FilePath, Container> containerMap = new HashMap<>();

    private final FileHandle handle;

    private final Map<FilePath, FileHandle> handles = new HashMap<>();

    protected Container(FileHandle handle) {
        this.handle = handle;
        this.increment();
    }

    public FilePath getPath() {
        return handle.getPath();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public FileHandle openFile(FilePath path) throws IOException {
        FilePath normalized = path.normalize();

        //noinspection AssertWithSideEffects
        assert (getPath() == null && normalized.getParent() == null) || (getPath().normalize().equals(normalized));

        synchronized (handles) {
            FileHandle h = handles.get(normalized);
            if (h != null) {
                h.increment();
                return h;
            }

            h = openFileImpl(path);
            if (h != null) {
                handles.put(normalized, h);
            }
            return h;
        }
    }

    protected FileHandle openFileImpl(FilePath path) throws IOException {
        return null; // TODO
    }

    public abstract NavigableSet<FilePath> resolveFiles() throws Exception;

    public boolean isReadonly() {
        return true;
    }

    protected void close() throws Exception {
    }

    @Override
    protected final void onRelease() {
        LOGGER.info("Release container " + this);

        synchronized (containerMap) {
            Container container = containerMap.remove(getPath().normalize());
            if (container != this) {
                throw new AssertionError("this is " + this + ", but container is" + container);
            }
        }

        try {
            this.close();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to close " + this, e);
        } finally {
            if (handle != null) {
                handle.decrement();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s[handle=%s]", this.getClass().getSimpleName(), getFileHandle());
    }
}
