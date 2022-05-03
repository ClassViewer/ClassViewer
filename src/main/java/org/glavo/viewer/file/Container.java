package org.glavo.viewer.file;

import org.glavo.viewer.util.ReferenceCounter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container extends ReferenceCounter implements Closeable {

    private final Map<FilePath, Container> containerMap = new ConcurrentHashMap<>();

    private final FilePath path;


    protected Container(FilePath path) {
        this.path = path;
    }

    public FilePath getPath() {
        return path;
    }

    public boolean isReadonly() {
        return true;
    }

    @Override
    protected final void cleanUp() {
        try {
            containerMap.remove(getPath()).close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close " + getPath(), e);
        }
    }

}
