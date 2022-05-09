package org.glavo.viewer.file;

import org.glavo.viewer.file.handles.PhysicalFileHandle;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.util.ReferenceCounter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container {

    private static final Map<FilePath, Container> containerMap = new HashMap<>();

    private final FileHandle handle;

    final Map<FilePath, FileHandle> fileHandles = new HashMap<>();
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

            FileHandle handle = path.getParent() == null
                    ? new PhysicalFileHandle(path)
                    : getContainer(path.getParent()).openFile(path);

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

    public FileHandle openFile(FilePath path) throws IOException {
        assert getPath() == null && path.getParent() == null || getPath().equals(path);

        synchronized (this) {
            FileHandle h = fileHandles.get(path);
            if (h != null) {
                return h.use();
            }

            h = openFileImpl(path);
            if (h != null) {
                fileHandles.put(path, h);
            }
            return h;
        }
    }

    protected abstract FileHandle openFileImpl(FilePath path) throws IOException;

    public abstract NavigableSet<FilePath> resolveFiles() throws Exception;

    public boolean isReadonly() {
        return true;
    }

    protected void closeImpl() throws Exception {
    }

    public synchronized void checkStatus() {
        if (fileHandles.isEmpty() && containerHandles.isEmpty()) {
            LOGGER.info("Release container " + this);

            synchronized (Container.class) {
                Container container = containerMap.remove(getPath());
                if (container != this) {
                    throw new AssertionError(String.format("expected=%s, actual=%s", this, container));
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
        }
    }

    @Override
    public String toString() {
        return String.format("%s[handle=%s]", this.getClass().getSimpleName(), getFileHandle());
    }
}
