package org.glavo.viewer.file2;

import org.glavo.viewer.file.Container;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class PhysicalFile extends VirtualFile {
    private final Container container;
    private final Path path;

    public PhysicalFile(Container container, Path path) {
        this.container = container;
        this.path = path.toAbsolutePath().normalize();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public List<String> relativize(VirtualFile other) {
        if (other instanceof PhysicalFile file && this.getContainer().equals(file.getContainer())) {
            Path relativized = this.path.relativize(file.path);
            String[] paths = new String[relativized.getNameCount()];
            for (int i = 0; i < paths.length; i++) {
                paths[i] = relativized.getName(i).toString();
            }
            return List.of(paths);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getFileName() {
        return path.getFileName().toString();
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof PhysicalFile other
                && this.container.equals(other.container)
                && this.path.equals(other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(container, path);
    }

    @Override
    public String toString() {
        return "PhysicalFile[container=%s, javaPath=%s]".formatted(container, path);
    }
}
