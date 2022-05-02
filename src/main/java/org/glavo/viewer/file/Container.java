package org.glavo.viewer.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Container implements Closeable {

    public abstract FilePath getPath();

    public abstract void close() throws IOException;

    private final Map<FilePath, Container> containerMap = new HashMap<>();


}
