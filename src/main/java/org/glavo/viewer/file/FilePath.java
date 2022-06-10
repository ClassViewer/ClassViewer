package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.File;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
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
