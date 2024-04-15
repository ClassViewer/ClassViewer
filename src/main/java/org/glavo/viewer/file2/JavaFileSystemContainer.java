/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Stream;

public abstract class JavaFileSystemContainer extends Container {

    private final FileSystem fileSystem;

    protected JavaFileSystemContainer(FileHandle handle, FileSystem fileSystem) {
        super(handle);
        this.fileSystem = fileSystem;
    }

    private Path toPath(VirtualFile file) {
        if (!(file instanceof JavaVirtualFile javaVirtualFile)) {
            throw new IllegalArgumentException("File " + file + " is not a JavaVirtualFile");
        }

        if (javaVirtualFile.getPath().getFileSystem() != fileSystem)
            throw new IllegalArgumentException("FileSystem mismatch");

        return javaVirtualFile.getPath();
    }

    protected abstract JavaVirtualFile createVirtualFile(Path path);

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
        try (Stream<Path> stream = Files.list(toPath(dir))) {
            return stream.<VirtualFile>map(this::createVirtualFile).toList();
        }
    }
}
