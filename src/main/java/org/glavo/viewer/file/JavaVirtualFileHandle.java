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

import org.glavo.viewer.file.roots.local.LocalFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

public abstract class JavaVirtualFileHandle extends FileHandle {
    private final SeekableByteChannel channel;

    protected JavaVirtualFileHandle(VirtualFile file, SeekableByteChannel channel) {
        super(file);
        this.channel = channel;
    }

    public Path getPath() {
        return ((LocalFile) file).getPath();
    }

    @Override
    public boolean supportRandomAccess() {
        return true;
    }

    @Override
    public SeekableByteChannel getChannel() throws IOException {
        channel.position(0);
        return channel;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        channel.position(0);
        return Channels.newInputStream(channel);
    }

    @Override
    protected void closeImpl() throws Exception {
        channel.close();
    }
}
