package org.glavo.viewer.file.roots.local;

import org.glavo.viewer.file.RootPath;
import org.glavo.viewer.file.TopPath;

public final class LocalRootPath extends RootPath {
    public static final LocalRootPath Path = new LocalRootPath();

    @Override
    protected TopPath createTopPath(String[] pathElements, boolean isDirectory) {
        return new LocalFilePath(pathElements, isDirectory);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public String toString() {
        return "[LocalRootPath]";
    }
}
