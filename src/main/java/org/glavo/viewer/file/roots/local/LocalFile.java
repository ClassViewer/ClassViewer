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
package org.glavo.viewer.file.roots.local;

import org.glavo.viewer.file.JavaVirtualFile;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

public final class LocalFile extends JavaVirtualFile {
    public LocalFile(Path path) {
        super(LocalRootContainer.CONTAINER, path);
        if (path.getFileSystem() == FileSystems.getDefault())
            throw new IllegalArgumentException(path + " is not a local file");
    }

    @Override
    public List<VirtualFile> listFiles() throws IOException {
        return super.listFilesNoSync();
    }

    @Override
    public String toString() {
        return "LocalFile[" + path + "]";
    }
}
