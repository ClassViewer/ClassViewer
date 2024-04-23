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
package org.glavo.viewer.file;

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
        if (!(other instanceof JavaVirtualFile file) || !this.getContainer().equals(file.getContainer())) {
            throw new IllegalArgumentException("Cannot relativize files of different containers");
        }

        Path relativized = this.path.relativize(file.path);
        if (relativized.getNameCount() == 1 && relativized.getName(0).toString().isEmpty()) {
            return List.of();
        }

        String[] paths = new String[relativized.getNameCount()];
        for (int i = 0; i < paths.length; i++) {
            String element = relativized.getName(i).toString();
            if (element.equals("..") || element.isEmpty()) {
                throw new IllegalArgumentException("this=%s, other=%s".formatted(this.path, file.path));
            }
            paths[i] = element;
        }
        return List.of(paths);
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
    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    protected final List<VirtualFile> listFilesNoSync() throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.<VirtualFile>map(path -> ((JavaFileSystemContainer) container).createVirtualFile(path)).toList();
        }
    }

    @Override
    public List<VirtualFile> listFiles() throws IOException {
        container.lock();
        try {
            container.ensureOpen();
            return listFilesNoSync();
        } finally {
            container.unlock();
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
