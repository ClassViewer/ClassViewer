package org.glavo.viewer.file.roots.local;

import org.glavo.viewer.file.RootPath;

public final class LocalRootPath extends RootPath {
    public static final LocalRootPath Path = new LocalRootPath();

    @Override
    protected int order() {
        return Integer.MIN_VALUE;
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
