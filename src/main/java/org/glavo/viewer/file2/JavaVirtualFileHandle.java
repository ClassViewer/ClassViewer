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

import org.glavo.viewer.file2.roots.local.LocalFile;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JavaVirtualFileHandle extends FileHandle {
    protected JavaVirtualFileHandle(VirtualFile file) {
        super(file);
    }

    public Path getPath() {
        return ((LocalFile) file).getPath();
    }

    @Override
    public boolean exists() {
        return Files.exists(getPath());
    }

    @Override
    public boolean isReadonly() {
        return !Files.isWritable(getPath());
    }

    @Override
    public SeekableByteChannel openChannel() throws IOException {
        return FileChannel.open(getPath());
    }

    @Override
    public SeekableByteChannel openWritableChannel() throws IOException {
        return FileChannel.open(getPath(), StandardOpenOption.WRITE);
    }
}
