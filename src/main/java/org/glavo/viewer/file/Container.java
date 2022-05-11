package org.glavo.viewer.file;

import org.glavo.viewer.file.containers.RootContainer;
import org.glavo.viewer.file.types.ContainerFileType;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container {

    private static final Map<FilePath, Container> containerMap = new HashMap<>();

    private final FileStubs handle;

    final Map<FilePath, FileStubs> fileStubs = new HashMap<>();
    final HashSet<ContainerHandle> containerHandles = new HashSet<>();

    protected Container(FileStubs handle) {
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

            FileStubs handle = path.getParent() == null
                    ? RootContainer.CONTAINER.openFile(path)
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

    public FileStubs getFileHandle() {
        return handle;
    }

    public synchronized FileStubs openFile(FilePath path) throws IOException {
        assert getPath() == null && path.getParent() == null || getPath().equals(path);

        FileStubs h = fileStubs.get(path);
        if (h != null) {
            return h.use();
        }

        h = openFileImpl(path);
        if (h != null) {
            fileStubs.put(path, h);
        }
        return h;
    }

    protected abstract FileStubs openFileImpl(FilePath path) throws IOException, NoSuchFileException;

    public abstract NavigableSet<FilePath> resolveFiles() throws Exception;

    public boolean isReadonly() {
        return true;
    }

    protected void closeImpl() throws Exception {
    }

    synchronized void checkStatus() {
        if (fileStubs.isEmpty() && containerHandles.isEmpty()) {
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
