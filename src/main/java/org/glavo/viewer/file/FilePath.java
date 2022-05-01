package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.glavo.viewer.util.JsonUtils;

import java.util.Objects;

@JsonIncludeProperties({"parent", "path", "type"})
public class FilePath {
    private final String path;
    private final FileType type;

    private final FilePath parent;

    private String fileName;

    public FilePath(String path, FileType type) {
        this(path, type, null);
    }

    @JsonCreator
    public FilePath(
            @JsonProperty("path") String path,
            @JsonProperty("type") FileType type,
            @JsonProperty("parent") FilePath parent) {
        this.path = path;
        this.type = type;
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public FileType getType() {
        return type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public FilePath getParent() {
        return parent;
    }

    public String getFileName() {
        if (fileName == null) {
            int idx = path.lastIndexOf('/');
            fileName = idx < 0 ? path : path.substring(idx + 1);
        }

        return fileName;
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
