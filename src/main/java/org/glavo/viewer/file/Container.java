package org.glavo.viewer.file;

import org.glavo.viewer.file.containers.RootContainer;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.util.ForceCloseable;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container implements ForceCloseable {

    private static final Map<FilePath, Container> containerMap = new HashMap<>();

    private final FileHandle handle;

    final Map<FilePath, FileStub> fileStubs = new HashMap<>();
    final HashSet<ContainerHandle> containerHandles = new HashSet<>();

    protected Container(FileHandle handle) {
        this.handle = handle;
    }

    public static Container getContainer(FilePath path) throws Throwable {
        synchronized (Container.class) {
            Container c = containerMap.get(path);

            if (c != null) {
                return c;
            }

            FileType type = FileType.detectFileType(path);
            if (!(type instanceof ContainerFileType)) {
                throw new UnsupportedOperationException("file " + path + " is not a container");
            }

            ContainerFileType ct = (ContainerFileType) type;

            Container container;

            FileHandle handle = new FileHandle(path.getParent() == null
                    ? RootContainer.CONTAINER.getStub(path)
                    : getContainer(path.getParent()).getStub(path));

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

    public FilePath getPath() {
        return handle.getPath();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public synchronized FileStub getStub(FilePath path) throws IOException {
        assert getPath() == null && path.getParent() == null || getPath().equals(path);

        FileStub h = fileStubs.get(path);
        if (h != null) {
            return h;
        }

        h = openFileImpl(path);
        if (h != null) {
            fileStubs.put(path, h);
        }
        return h;
    }

    protected abstract FileStub openFileImpl(FilePath path) throws IOException, NoSuchFileException;

    public abstract NavigableSet<FilePath> resolveFiles() throws Exception;

    public boolean isReadonly() {
        return true;
    }

    protected void closeImpl() throws Exception {
    }

    synchronized void checkStatus() {
        if (fileStubs.isEmpty() && containerHandles.isEmpty()) {
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

        for (FileStub stub : new ArrayList<>(this.fileStubs.values())) {
            stub.forceClose();
        }
        if (!fileStubs.isEmpty()) {
            throw new AssertionError("fileStubs=" + fileStubs);
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
