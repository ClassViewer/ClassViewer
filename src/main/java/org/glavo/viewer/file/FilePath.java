package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.glavo.viewer.file.roots.local.LocalFilePath;
import org.jetbrains.annotations.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = DefaultFilePath.class, name = "default"),
        @JsonSubTypes.Type(value = LocalFilePath.class, name = "local"),
})
public abstract class FilePath implements Comparable<FilePath> {
    private final String[] pathElements;
    private final boolean isDirectory;
    private final String path;
    private final FilePath parent;

    public FilePath(String[] pathElements, boolean isDirectory, FilePath parent) {
        this.pathElements = pathElements;
        this.isDirectory = isDirectory;
        this.parent = parent;

        this.path = String.join("/", pathElements);
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public FilePath getParent() {
        return parent;
    }

    public String getPath() {
        return path;
    }

    String[] getPathElements() {
        return pathElements;
    }


    @Override
    public int compareTo(@NotNull FilePath o) {
        return 0;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.getPath().hashCode() + getPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FilePath that
                && this.getClass() == that.getClass()
                && getPath().equals(that.getPath());
    }
}
