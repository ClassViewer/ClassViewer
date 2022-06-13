package org.glavo.viewer.file.roots.local;

import org.glavo.viewer.file.RootPath;

public final class LocalRootPath extends RootPath {
    public static final LocalRootPath Path = new LocalRootPath();

    @Override
    public boolean isDirectory() {
        return true;
    }
}
