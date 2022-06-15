package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.glavo.viewer.file.roots.local.LocalRootPath;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

@JsonIncludeProperties({"parent", "path", "isDirectory"})
@JsonPropertyOrder({"parent", "path", "isDirectory"})
public final class FilePath extends AbstractPath {
    private final String[] pathElements;
    private final boolean isDirectory;
    private final String path;
    private final AbstractPath parent;

    public FilePath(String[] pathElements, boolean isDirectory, AbstractPath parent) {
        this.pathElements = pathElements;
        this.isDirectory = isDirectory;
        this.parent = parent;

        this.path = String.join("/", pathElements);
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


    @Override
    public int compareTo(@NotNull AbstractPath o) {
        if (!(o instanceof FilePath p)) {
            int res = this.getRoot().compareTo(o);
            return res == 0 ? 1 : res;
        }

        if (this.getParent() instanceof RootPath r1 && p.getParent() instanceof RootPath r2) {
            int c = r1.compareTo(r2);
            if (c != 0) return c;

            return Arrays.compare(this.getPathElements(), p.getPathElements());
        }
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
