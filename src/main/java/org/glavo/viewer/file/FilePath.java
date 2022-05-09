package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.property.StringProperty;
import kala.platform.OperatingSystem;
import kala.platform.Platform;
import org.glavo.viewer.util.ArrayUtils;
import org.glavo.viewer.util.JsonUtils;

import java.io.File;
import java.nio.file.Path;
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

    public FilePath(String path, FilePath parent) {
        this(path, false, parent);
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

    public FilePath(String[] pathElements) {
        this(pathElements, false, null);
    }

    public FilePath(String[] pathElements, FilePath parent) {
        this(pathElements, false, parent);
    }

    public FilePath(String[] pathElements, boolean isDirectory, FilePath parent) {
        this.isDirectory = isDirectory;
        this.parent = parent;
        this.pathElements = pathElements;

        StringBuilder builder = new StringBuilder();
        if (parent == null) {
            if (Platform.CURRENT_SYSTEM != OperatingSystem.WINDOWS) {
                builder.append('/');
            }
        } else if (!parent.getParent().isDirectory()) {
            builder.append('/');
        }

        builder.append(String.join("/", pathElements));
        this.path = builder.toString();
    }

    public static FilePath ofJavaPath(Path p) {
        return ofJavaPath(p, false);
    }

    public static FilePath ofJavaPath(Path p, boolean isDirectory) {
        return new FilePath(p.normalize().toAbsolutePath().toString(), isDirectory, null);
    }

    public String getPath() {
        return path;
    }

    @JsonProperty("isDirectory")
    public boolean isDirectory() {
        return isDirectory;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public FilePath getParent() {
        return parent;
    }

    public FilePath getParentContainerPath() {
        FilePath p;
        do {
            p = getParent();
        } while (p != null && p.isDirectory());

        return p;
    }

    public boolean isDefaultFileSystemPath() {
        return parent == null;
    }

    public String getFileName() {
        return pathElements[pathElements.length - 1];
    }

    private String extension;
    public String getFileNameExtension() {
        if (extension == null) {
            String fn = getFileName();
            int idx = fn.lastIndexOf('.');
            extension = idx <= 0 ? "" : fn.substring(idx + 1).toLowerCase(Locale.ROOT);
        }

        return extension;
    }

    public String[] getPathElements() {
        return pathElements;
    }

    public String[] relativize(FilePath other) {
        if (this.equals(other.getParent())) {
            return other.getPathElements();
        } else if (Objects.equals(this.getParent(), other.getParent())) {
            if (!this.isDirectory()) {
                throw new UnsupportedOperationException(this + " is not folder path");
            }
            if (!ArrayUtils.isPrefix(other.getPathElements(), this.getPathElements())) {
                throw new UnsupportedOperationException(this + " is not prefix of " + other);
            }

            return Arrays.copyOfRange(other.getPathElements(), this.getPathElements().length, other.getPathElements().length);
        } else {
            throw new UnsupportedOperationException(String.format("this=%s, other=%s", this, other));
        }
    }

    /*
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

     */

    @Override
    public int compareTo(FilePath other) {
        if (this.equals(other)) {
            return 0;
        }

        if (this.parent != null) {
            if (other.getParent() == null) {
                return 1;
            } else {
                int res = this.getParent().compareTo(other);
                if (res != 0) {
                    return res;
                }
            }
        } else if (other.getParent() != null) {
            return -1;
        }

        final int normalizedLength = this.getPathElements().length;
        final int otherLength = other.getPathElements().length;

        int length = Math.min(normalizedLength, otherLength);
        for (int i = 0; i < length; i++) {
            int v = this.getPathElements()[i].compareTo(other.getPathElements()[i]);
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
