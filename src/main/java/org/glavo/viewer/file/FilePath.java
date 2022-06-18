package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.*;
import kala.platform.OperatingSystem;
import kala.platform.Platform;
import org.glavo.viewer.file.roots.local.LocalRootPath;
import org.glavo.viewer.util.ArrayUtils;
import org.glavo.viewer.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@JsonIncludeProperties({"parent", "path", "isDirectory"})
@JsonPropertyOrder({"parent", "path", "isDirectory"})
public final class FilePath extends AbstractPath {

    private final String[] pathElements;
    private final String path;
    private final AbstractPath parent;

    private final boolean isDirectory;
    private final byte level;

    public FilePath(String[] pathElements, boolean isDirectory, AbstractPath parent) {
        this.pathElements = pathElements;
        this.isDirectory = isDirectory;
        this.parent = parent;
        this.level = (byte) (parent instanceof FilePath p ? p.getLevel() + 1 : 1);

        if (parent == LocalRootPath.Path && Platform.CURRENT_SYSTEM == OperatingSystem.WINDOWS) {
            this.path = String.join("/", pathElements);
            this.str = path;
        } else {
            this.path = "/" + String.join("/", pathElements);
        }
    }

    public static FilePath of(
            @JsonProperty("path") String path,
            @JsonProperty("isDirectory") boolean isDirectory,
            @JsonProperty("parent") AbstractPath parent
    ) {
        return new FilePath(StringUtils.spiltPath(path), isDirectory, Objects.requireNonNullElse(parent, LocalRootPath.Path));
    }

    public static FilePath ofJavaPath(Path p) {
        return ofJavaPath(p, Files.isDirectory(p));
    }

    public static FilePath ofJavaPath(Path p, boolean isDirectory) {
        return of(p.normalize().toAbsolutePath().toString(), isDirectory, null);
    }

    int getLevel() {
        return level;
    }

    @JsonProperty("isDirectory")
    public boolean isDirectory() {
        return isDirectory;
    }

    public AbstractPath getParent() {
        return parent;
    }

    @JsonProperty("parent")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public AbstractPath serializationParent() {
        return getParent() == LocalRootPath.Path ? null : getParent();

    }

    public FilePath getParentFilePath() {
        return getParent() instanceof FilePath p ? p : null;
    }

    public String getPath() {
        return path;
    }

    public RootPath getRoot() {
        FilePath path = this;
        do {
            AbstractPath p = path.getParent();
            if (p instanceof RootPath root) return root;
            path = ((FilePath) p);
        } while (true);
    }

    public boolean isLocalFile() {
        return getParent() == LocalRootPath.Path;
    }

    public String getFileName() {
        return pathElements.length == 0 ? "" : pathElements[pathElements.length - 1];
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

    public String[] getPathElements() {
        return pathElements;
    }

    private static int compare(FilePath p1, FilePath p2, int level) {
        int c = level == 1
                ? p1.getParent().compareTo(p2.getParent())
                : compare((FilePath) p1.getParent(), (FilePath) p2.getParent(), level - 1);

        return c != 0 ? c : Arrays.compare(p1.getPathElements(), p2.getPathElements());
    }

    @Override
    public int compareTo(AbstractPath o) {
        if (!(o instanceof FilePath p)) {
            int res = this.getRoot().compareTo(o);
            return res == 0 ? 1 : res;
        }

        FilePath p1 = this;
        FilePath p2 = p;

        if (p1.getLevel() > p2.getLevel()) {
            while (p1.getLevel() != p2.getLevel()) {
                p1 = (FilePath) p1.getParent();
            }
        } else if (p1.getLevel() < p2.getLevel()) {
            while (p1.getLevel() != p2.getLevel()) {
                p2 = (FilePath) p2.getParent();
            }
        }

        int c = compare(p1, p2, p1.getLevel());
        if (c == 0) c = Integer.compare(this.getLevel(), p.getLevel());
        if (c == 0 && this.isDirectory() != p.isDirectory()) c = this.isDirectory() ? -1 : 1;

        return c;
    }

    @Override
    public int hashCode() {
        return this.getPath().hashCode() + getParent().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FilePath that
                && Objects.equals(this.getParent(), that.getParent())
                && getPath().equals(that.getPath());
    }

    private String str;

    @Override
    public String toString() {
        if (str == null) {
            str = getParent() + "/" + getPath();
        }

        return str;
    }
}
