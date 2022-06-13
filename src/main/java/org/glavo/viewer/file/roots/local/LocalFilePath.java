package org.glavo.viewer.file.roots.local;

import kala.platform.OperatingSystem;
import kala.platform.Platform;
import org.glavo.viewer.file.RootPath;
import org.glavo.viewer.file.TopFilePath;

public final class LocalFilePath extends TopFilePath {

    public LocalFilePath(String[] pathElements, String path, boolean isDirectory) {
        super(pathElements, isDirectory, LocalRootPath.Path);
    }

    @Override
    public RootPath getRootPath() {
        return LocalRootPath.Path;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public String toString() {
        if (str == null) {
            str = Platform.CURRENT_SYSTEM == OperatingSystem.WINDOWS ? getPath() : "/" + getPath();
        }
        return str;
    }
}
