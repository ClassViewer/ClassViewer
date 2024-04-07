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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class JavaVirtualFile extends VirtualFile {
    protected final Path path;

    protected JavaVirtualFile(Container container, Path path) {
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
    public String toString() {
        return "JavaVirtualFile[container=%s, path=%s]".formatted(container, path);
    }
}
