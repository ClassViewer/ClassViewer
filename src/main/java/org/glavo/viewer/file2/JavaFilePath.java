package org.glavo.viewer.file2;

import org.glavo.viewer.file.Container;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class JavaFilePath implements FilePath {
    private final Container container;
    private final Path javaPath;


    public JavaFilePath(Container container, Path javaPath) {
        this.container = container;
        this.javaPath = javaPath.toAbsolutePath().normalize();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public List<String> relativize(FilePath other) {
        if (other instanceof JavaFilePath path && this.getContainer().equals(path.getContainer())) {
            Path relativized = this.javaPath().relativize(path.javaPath());
            String[] paths = new String[relativized.getNameCount()];
            for (int i = 0; i < paths.length; i++) {
                paths[i] = relativized.getName(i).toString();
            }
            return List.of(paths);
        }
        throw new IllegalArgumentException();
    }

    public Path javaPath() {
        return javaPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof JavaFilePath other
                && this.container.equals(other.container)
                && this.javaPath.equals(other.javaPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(container, javaPath);
    }

    @Override
    public String toString() {
        return "JavaFilePath[container=%s, javaPath=%s]".formatted(container, javaPath);
    }
}
