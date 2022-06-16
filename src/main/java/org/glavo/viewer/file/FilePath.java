package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kala.platform.OperatingSystem;
import kala.platform.Platform;
import org.glavo.viewer.file.roots.local.LocalRootPath;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

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

    String[] getPathElements() {
        return pathElements;
    }

    private static int compare(FilePath p1, FilePath p2, int level) {
        int c = level == 1
                ? p1.getParent().compareTo(p2.getParent())
                : compare((FilePath) p1.getParent(), (FilePath) p2.getParent(), level - 1);

        return c != 0 ? c : Arrays.compare(p1.getPathElements(), p2.getPathElements());
    }

    @Override
    public int compareTo(@NotNull AbstractPath o) {
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
        return c != 0 ? c : Integer.compare(this.getLevel(), p.getLevel());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.getPath().hashCode() + getPath().hashCode();
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
