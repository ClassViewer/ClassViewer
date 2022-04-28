package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.glavo.viewer.util.JsonUtils;

import java.util.Objects;

public class FilePath {
    private final String path;
    private final FileType type;

    private final FilePath parent;

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

    public FilePath getParent() {
        return parent;
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
