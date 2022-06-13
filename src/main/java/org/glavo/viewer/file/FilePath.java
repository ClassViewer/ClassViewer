package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.glavo.viewer.file.root.RootPath;
import org.glavo.viewer.file.root.local.LocalFilePath;

import java.io.File;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = LocalFilePath.class, name = "local"),
})
public interface FilePath {
    String getPath();

    boolean isDirectory();

    RootPath getRootPath();

    default boolean isFile() {
        return !isDirectory();
    }

    default File toJavaFile() {
        throw new UnsupportedOperationException();
    }
}
