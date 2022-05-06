package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.*;

import java.io.File;
import java.util.*;

@JsonIncludeProperties({"parent", "path", "isDirectory"})
@JsonPropertyOrder({"parent", "path", "isDirectory"})
public class FilePath implements Comparable<FilePath> {

    private final String[] pathElements;
    private final String path;
    private final boolean isDirectory;

    private final FilePath parent;

    public FilePath(String path) {
        this(path, false, null);
    }

    @JsonCreator
    public FilePath(
            @JsonProperty("path") String path,
            @JsonProperty("isDirectory") boolean isDirectory,
            @JsonProperty("parent") FilePath parent) {
        this.path = path.replace('\\', '/');
        this.isDirectory = isDirectory;
        this.parent = parent;
        this.pathElements = this.path.split("/");
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
        return parent == null;
    }

    public String getFileName() {
        return pathElements[pathElements.length - 1];
    }

    public String[] getPathElements() {
        return pathElements;
    }

    private FilePath normalized;

    public FilePath normalize() {
        if (normalized == null) {
            if (parent == null) {
                normalized = this;
            } else {
                FilePath p = parent.normalize();
                if (p.isDirectory()) {
                    normalized = new FilePath(p.getPath() + '/' + this.getPath(), this.isDirectory(), p.getParent());
                } else if (p == parent) {
                    normalized = this;
                } else {
                    normalized = new FilePath(this.getPath(), this.isDirectory(), parent);
                }
            }
        }
        return normalized;
    }

    @Override
    public int compareTo(FilePath other) {
        FilePath normalized = this.normalize();
        other = other.normalize();

        if (normalized.equals(other)) {
            return 0;
        }

        if (normalized.parent != null) {
            if (other.getParent() == null) {
                return 1;
            } else {
                int res = normalized.parent.compareTo(other);
                if (res != 0) {
                    return res;
                }
            }
        } else if (other.parent != null) {
            return -1;
        }

        final int normalizedLength = normalized.pathElements.length;
        final int otherLength = other.pathElements.length;

        int length = Math.min(normalizedLength, otherLength);
        for (int i = 0; i < length; i++) {
            int v = normalized.pathElements[i].compareTo(other.pathElements[i]);
            if (v != 0) {
                return v;
            }
        }

        return normalizedLength - otherLength;
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

    private String str;

    private StringBuilder toString(StringBuilder builder) {
        if (parent != null) {
            parent.toString(builder);
            if (parent.isDirectory()) {
                builder.append('/');
            } else {
                builder.append('!');
            }
        }

        builder.append(path);
        return builder;
    }

    @Override
    public String toString() {
        if (str == null) {
            str = parent == null ? path : toString(new StringBuilder()).toString();
        }

        return str;
    }
}
