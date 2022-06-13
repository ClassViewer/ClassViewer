package org.glavo.viewer.file.root.local;

import org.glavo.viewer.file.root.RootPath;

public final class LocalRootPath implements RootPath {
    public static final LocalRootPath Path = new LocalRootPath();


    @Override
    public String getPath() {
        return null;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
