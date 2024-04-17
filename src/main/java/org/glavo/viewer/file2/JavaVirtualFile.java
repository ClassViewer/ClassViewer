/*
 * Copyright (C) 2024 Glavo. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.glavo.viewer.file2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class JavaVirtualFile extends VirtualFile {
    protected final Path path;

    protected JavaVirtualFile(JavaFileSystemContainer container, Path path) {
        super(container);
        this.path = path.toAbsolutePath().normalize();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public List<String> relativize(VirtualFile other) {
        if (other instanceof JavaVirtualFile file && this.getContainer().equals(file.getContainer())) {
            Path relativized = this.path.relativize(file.path);
            if (relativized.getNameCount() == 1) {
                return List.of(relativized.getName(0).toString());
            }
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
    public VirtualFile getParent() {
        Path parent = path.getParent();
        return parent != null ? ((JavaFileSystemContainer) container).createVirtualFile(parent) : null;
    }

    @Override
    protected FileHandle open() throws IOException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        if (!Files.isRegularFile(path)) {
            throw new IOException(path + " is not a regular file");
        }

        if (!Files.isReadable(path)) {
            throw new IOException(path + " is not readable");
        }

        return ((JavaFileSystemContainer) container).createVirtualFileHandle(this);
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public List<VirtualFile> listFiles() throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.<VirtualFile>map(path -> ((JavaFileSystemContainer) container).createVirtualFile(path)).toList();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(container, path);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj != null && this.getClass() == obj.getClass() && this.path.equals(((JavaVirtualFile) obj).path));
    }

    @Override
    public String toString() {
        return "JavaVirtualFile[container=%s, path=%s]".formatted(container, path);
    }
}
