package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@JsonIncludeProperties({"parent", "path", "isDirectory"})
@JsonPropertyOrder({"parent", "path", "isDirectory"})
public class FilePath implements Comparable<FilePath> {

    private final String[] pathElements;
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
        this.pathElements = path.split(File.separatorChar == '\\' ? "\\\\" : File.separator);
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
    public int compareTo(FilePath other) {
        if (this.equals(other)) {
            return 0;
        }

        if (this.parent != null) {
            if (other.getParent() == null) {
                return 1;
            } else {
                int res = this.parent.compareTo(other);
                if (res != 0) {
                    return res;
                }
            }
        } else if (other.parent != null) {
            return -1;
        }

        final int thisLength = this.pathElements.length;
        final int otherLength = other.pathElements.length;

        int length = Math.min(thisLength, otherLength);
        for (int i = 0; i < length; i++) {
            int v = this.pathElements[i].compareTo(other.pathElements[i]);
            if (v != 0) {
                return v;
            }
        }

        return thisLength - otherLength;
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

    private void toString(StringBuilder builder) {
        if (parent != null) {
            parent.toString(builder);
            builder.append('!');
        }
        builder.append(path);
    }

    @Override
    public String toString() {
        if (parent == null) {
            return path;
        }

        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }
}
