package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIncludeProperties({"parent", "path", "isDirectory"})
@JsonPropertyOrder({"parent", "path", "isDirectory"})
public final class NestedFilePath extends FilePath {
    private final String[] pathElements;
    private final boolean isDirectory;
    private final FilePath parent;

    public NestedFilePath(String[] pathElements, boolean isDirectory, FilePath parent) {
        this.pathElements = pathElements;
        this.isDirectory = isDirectory;
        this.parent = parent;
    }

    @Override
    @JsonProperty("isDirectory")
    public boolean isDirectory() {
        return isDirectory;
    }

    public FilePath getParent() {
        return parent;
    }

    @Override
    public RootPath getRootPath() {
        return getParent().getRootPath();
    }

    private String path;

    public String getPath() {
        if (path == null) {
            path = String.join("/", pathElements);
        }

        return path;
    }

    private String str;

    @Override
    public String toString() {
        if (str == null) {
            str = ""; // TODO
        }
        return str;
    }
}
