package org.glavo.viewer.file;

import org.glavo.viewer.util.ReferenceCounter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container extends ReferenceCounter implements Closeable {

    private static final Map<FilePath, Container> containerMap = new ConcurrentHashMap<>();

    private final FilePath path;
    private final Container parent;

    protected Container(Container parent, FilePath path) {
        this.path = path;
        this.parent = parent;
    }

    public FilePath getPath() {
        return path;
    }

    public Container getParent() {
        return parent;
    }

    public abstract NavigableSet<FilePath> resolveFiles();

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
            parent.decrement();
        }
    }

    @Override
    public String toString() {
        return String.format("%s[parent=%s, path=%s]", this.getClass().getSimpleName(), getParent(), getPath());
    }
}
