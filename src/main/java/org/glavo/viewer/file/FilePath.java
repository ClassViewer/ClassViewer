package org.glavo.viewer.file;

import java.io.File;

public interface FilePath {
    String getPath();

    boolean isDirectory();

    default boolean isFile() {
        return !isDirectory();
    }

    default File toJavaFile() {
        throw new UnsupportedOperationException();
    }
}
