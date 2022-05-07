package org.glavo.viewer.file;

import org.glavo.viewer.file.types.ContainerFileType;
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

    public static Container openContainer(FilePath path) throws Throwable {
        FileType type = FileType.detectFileType(path);
        if (!(type instanceof ContainerFileType)) {
            throw new AssertionError();
        }

        return openContainer((ContainerFileType) type, path);
    }

    public static Container openContainer(ContainerFileType type, FilePath path) throws Throwable {
        synchronized (containerMap) {
            Container c = containerMap.get(path);

            if (c != null) {
                c.increment();
                return c;
            }

            return null; // TODO

        }
    }

    public FilePath getPath() {
        return handle.getPath();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public FileHandle openFile(FilePath path) throws IOException {
        assert (getPath() == null && path.getParent() == null) || (getPath().equals(path));

        synchronized (handles) {
            FileHandle h = handles.get(path);
            if (h != null) {
                h.increment();
                return h;
            }

            h = openFileImpl(path);
            if (h != null) {
                handles.put(path, h);
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
            Container container = containerMap.remove(getPath());
            if (container != this) {
                throw new AssertionError("this is " + this + ", but container is " + container);
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
