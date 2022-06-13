package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.glavo.viewer.file.roots.local.LocalFilePath;

import java.io.File;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LocalFilePath.class, name = "local"),
        @JsonSubTypes.Type(value = NestedPath.class, name = "nested")
})
public abstract class FilePath {
    public abstract boolean isDirectory();

    public abstract RootPath getRootPath();

    public boolean isFile() {
        return !isDirectory();
    }

    public File toJavaFile() {
        throw new UnsupportedOperationException();
    }
}
