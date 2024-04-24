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
package org.glavo.viewer.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    protected abstract JavaVirtualFileHandle createVirtualFileHandle(JavaVirtualFile file, SeekableByteChannel channel);

    @Override
    public boolean hasMultiRoots() {
        return true;
    }

    @Override
    public VirtualFile getRootDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VirtualFile> getRootDirectories() {
        ArrayList<VirtualFile> res = new ArrayList<>(1);
        for (Path rootDirectory : fileSystem.getRootDirectories()) {
            res.add(createVirtualFile(rootDirectory));
        }
        return res;
    }

    @Override
    protected FileHandle openFileImpl(VirtualFile file) throws IOException {
        Path path = toPath(file);
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        return createVirtualFileHandle((JavaVirtualFile) file, Files.newByteChannel(path));
    }
}
