package org.glavo.viewer.file2;

import kala.platform.OperatingSystem;
import kala.platform.Platform;
import org.glavo.viewer.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class FilePath {

    private final List<String> pathElements;
    private final String path;
    private final FilePath parent;

    private FilePath(List<String> pathElements, boolean isDirectory, FilePath parent) {
        this.pathElements = pathElements;
        this.parent = parent;

        if (parent == null && Platform.CURRENT_SYSTEM == OperatingSystem.WINDOWS) {
            this.path = String.join("/", pathElements);
            this.str = path;
        } else {
            this.path = "/" + String.join("/", pathElements);
        }
    }

    public static FilePath of(String path, boolean isDirectory, FilePath parent) {
        return new FilePath(List.of(StringUtils.spiltPath(path)), isDirectory, parent);
    }

    public static FilePath ofJavaPath(Path p) {
        return ofJavaPath(p, Files.isDirectory(p));
    }

    public static FilePath ofJavaPath(Path p, boolean isDirectory) {
        return of(p.normalize().toAbsolutePath().toString(), isDirectory, null);
    }

    public FilePath getParent() {
        return parent;
    }

    public FilePath getParentFilePath() {
        return getParent() instanceof FilePath p ? p : null;
    }

    public String getPath() {
        return path;
    }

    public boolean isLocalFile() {
        return parent == null;
    }

    public String getFileName() {
        return pathElements.isEmpty() ? "" : pathElements.getLast();
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

    public List<String> getPathElements() {
        return pathElements;
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
