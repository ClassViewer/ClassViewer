package org.glavo.viewer.file.root.local;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.root.RootPath;

public final class LocalFilePath implements FilePath {


    @Override
    public RootPath getRootPath() {
        return LocalRootPath.Path;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
