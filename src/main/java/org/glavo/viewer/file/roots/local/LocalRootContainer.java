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

import org.glavo.viewer.file.*;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public final class LocalRootContainer extends JavaFileSystemContainer {
    public static final LocalRootContainer CONTAINER = new LocalRootContainer();
    private static final ContainerHandle IGNORED = new ContainerHandle(CONTAINER); // should not be closed

    private LocalRootContainer() {
        super(null, FileSystems.getDefault());
    }

    @Override
    protected LocalFile createVirtualFile(Path path) {
        return new LocalFile(path);
    }

    protected JavaVirtualFileHandle createVirtualFileHandle(JavaVirtualFile file, SeekableByteChannel channel) {
        return new LocalFileHandle((LocalFile) file, channel);
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
