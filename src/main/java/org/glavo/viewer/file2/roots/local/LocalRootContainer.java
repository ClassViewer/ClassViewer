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
package org.glavo.viewer.file2.roots.local;

import org.glavo.viewer.file2.Container;
import org.glavo.viewer.file2.ContainerHandle;
import org.glavo.viewer.file2.FileHandle;
import org.glavo.viewer.file2.VirtualFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Stream;

public final class LocalRootContainer extends Container {
    public static final LocalRootContainer CONTAINER = new LocalRootContainer();
    private static final ContainerHandle ignored = new ContainerHandle(CONTAINER); // should not be closed

    private LocalRootContainer() {
        super(null);
    }

    private static Path toPath(VirtualFile file) {
        if (!(file instanceof LocalFile localFile)) {
            throw new IllegalArgumentException("File " + file + " is not a LocalFile");
        }

        return localFile.getPath();
    }

    @Override
    protected FileHandle openFileImpl(VirtualFile file) throws IOException, NoSuchFileException {
        Path path = toPath(file);
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        if (!Files.isReadable(path)) {
            throw new IOException(path + " is not readable");
        }

        return null;
    }

    @Override
    public NavigableSet<VirtualFile> resolveFiles() throws IOException {
        return null; // TODO
    }

    @Override
    public List<VirtualFile> list(VirtualFile dir) throws IOException {
        if (!(dir instanceof LocalFile localDir)) {
            throw new IllegalArgumentException(dir + " is not a LocalFile");
        }

        try (Stream<Path> stream = Files.list(localDir.getPath())) {
            return stream.<VirtualFile>map(LocalFile::new).toList();
        }
    }

    @Override
    public void closeImpl() {
        throw new UnsupportedOperationException("LocalContainer should not be closed");
    }

    @Override
    public String toString() {
        return "LocalRootContainer";
    }
}
