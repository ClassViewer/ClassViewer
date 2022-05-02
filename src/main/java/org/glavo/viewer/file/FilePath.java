package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.glavo.viewer.util.JsonUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@JsonIncludeProperties({"parent", "path", "isDirectory"})
@JsonPropertyOrder({"parent", "path", "isDirectory"})
public class FilePath {
    private final String path;
    private final boolean isDirectory;

    private final FilePath parent;
    private String fileName;

    private Path javaPath;

    public FilePath(String path) {
        this(path, false, null);
    }

    @JsonCreator
    public FilePath(
            @JsonProperty("path") String path,
            @JsonProperty("isDirectory") boolean isDirectory,
            @JsonProperty("parent") FilePath parent) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public FilePath getParent() {
        return parent;
    }

    public boolean isDefaultFileSystemPath() {
        return path == null;
    }

    public String getFileName() {
        if (fileName == null) {
            int idx = path.lastIndexOf('/');
            fileName = idx < 0 ? path : path.substring(idx + 1);
        }

        return fileName;
    }

    public Path getJavaPath() {
        if (javaPath == null) {
            if (parent != null) {
                throw new AssertionError();
            }

            javaPath = Paths.get(path);
        }
        return javaPath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, parent);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FilePath)) {
            return false;
        }

        FilePath that = (FilePath) obj;
        return Objects.equals(this.parent, that.parent) && this.path.equals(that.path);
    }

    @Override
    public String toString() {
        try {
            return JsonUtils.MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        }
    }
}
