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

import org.glavo.viewer.file.roots.local.LocalFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

public abstract non-sealed class VirtualFile extends VirtualPath {

    public static VirtualFile of(File file) {
        return new LocalFile(file.toPath());
    }

    public static VirtualFile of(Path path) {
        if (path.getFileSystem() == FileSystems.getDefault()) {
            return new LocalFile(path);
        } else {
            throw new IllegalArgumentException("Unsupported path: " + path);
        }
    }

    protected static List<String> relativize(List<String> base, List<String> other) {
        if (base.size() > other.size()) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < base.size(); i++) {
            if (!other.get(i).equals(base.get(i))) {
                throw new IllegalArgumentException(base + " is not prefix of " + other);
            }
        }

        return other.subList(base.size(), other.size());
    }

    protected final Container container;

    protected VirtualFile(Container container) {
        this.container = container;
    }

    public final @NotNull Container getContainer() {
        return container;
    }

    /**
     * @throws IllegalArgumentException if other is not a Path that can be relativized against this path
     */
    public abstract List<String> relativize(VirtualFile other);

    public abstract String getFileName();

    public abstract VirtualFile getParent();

    // ---

    public abstract boolean isDirectory();

    public abstract List<? extends VirtualFile> listFiles() throws IOException;
}
