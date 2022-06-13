package org.glavo.viewer.file.root;

import org.glavo.viewer.file.FilePath;

public interface RootPath extends FilePath {
    @Override
    default RootPath getRootPath() {
        return this;
    }
}
