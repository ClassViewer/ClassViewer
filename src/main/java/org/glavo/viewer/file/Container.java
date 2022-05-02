package org.glavo.viewer.file;

import org.glavo.viewer.util.ReferenceCounter;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public abstract class Container extends ReferenceCounter implements Closeable {

    private final Map<FilePath, Container> containerMap = new HashMap<>();

    public abstract FilePath getPath();

    public boolean isReadonly() {
        return true;
    }

}
