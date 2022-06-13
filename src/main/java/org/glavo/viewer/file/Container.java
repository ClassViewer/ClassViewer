package org.glavo.viewer.file;

import org.glavo.viewer.file.roots.local.LocalContainer;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.util.ForceCloseable;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container implements ForceCloseable {

    private static final Map<OldFilePath, Container> containerMap = new HashMap<>();

    private final FileHandle handle;

    final Map<OldFilePath, FileHandle> handles = new HashMap<>();
    final HashSet<ContainerHandle> containerHandles = new HashSet<>();

    protected Container(FileHandle handle) {
        this.handle = handle;
    }

    public static Container getContainer(OldFilePath path) throws Throwable {
        if (path == null) {
            return LocalContainer.CONTAINER;
        }
        synchronized (Container.class) {
            Container c = containerMap.get(path);

            if (c != null) {
                return c;
            }

            FileType type = FileType.detectFileType(path);
            if (!(type instanceof ContainerFileType ct)) {
                throw new UnsupportedOperationException("file " + path + " is not a container");
            }

            Container container;

            FileHandle handle = getContainer(path.getParent()).openFile(path);

            try {
                container = ct.openContainerImpl(handle);
            } catch (Throwable ex) {
                handle.close();
                throw ex;
            }

            containerMap.put(path, container);
            return container;
        }
    }

    public static Container getContainerOrNull(OldFilePath path) {
        try {
            return Container.getContainer(path);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to open container", e);
            return null;
        }
    }

    public OldFilePath getPath() {
        return handle.getPath();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public synchronized FileHandle openFile(OldFilePath path) throws IOException {
        assert getPath() == null && path.getParent() == null || getPath().equals(path);

        FileHandle h = handles.get(path);
        if (h != null) {
            throw new UnsupportedOperationException("Open file " + path + " repeatedly");
        }

        h = openFileImpl(path);
        if (h != null) {
            handles.put(path, h);
        }
        return h;
    }

    protected abstract FileHandle openFileImpl(OldFilePath path) throws IOException, NoSuchFileException;

    public final NavigableSet<OldFilePath> resolveFiles() throws Exception {return null;}

    public abstract Set<OldFilePath> list(OldFilePath dir);

    public boolean isReadonly() {
        return true;
    }

    protected void closeImpl() throws Exception {
    }

    synchronized void checkStatus() {
        if (handles.isEmpty() && containerHandles.isEmpty()) {
            forceClose();
        }
    }

    private boolean closed = false;

    public synchronized void forceClose() {
        if (closed) {
            return;
        }
        closed = true;

        LOGGER.info("Close container " + this);

        synchronized (Container.class) {
            Container container = containerMap.remove(getPath());
            if (container != this) {
                throw new AssertionError(String.format("expected=%s, actual=%s", this, container));
            }
        }

        for (FileHandle handle : new ArrayList<>(this.handles.values())) {
            handle.forceClose();
        }
        if (!handles.isEmpty()) {
            throw new AssertionError("handles=" + handles);
        }

        for (ContainerHandle handle : new ArrayList<>(this.containerHandles)) {
            handle.forceClose();
        }
        if (!this.containerHandles.isEmpty()) {
            throw new AssertionError("containerHandles=" + containerHandles);
        }

        try {
            this.closeImpl();
        } catch (Throwable e) {
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
