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
package org.glavo.viewer.file.containers.jimage;

import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;

public final class JImageFileHandle extends FileHandle {
    JImageFileHandle(VirtualFile file) {
        super(file);
    }

    @Override
    public boolean supportRandomAccess() {
        return true;
    }

    private ImageReader getReader() {
        return ((JImageContainer) file.getContainer()).getReader();
    }

    private ImageReader.Node getNode() {
        return ((JImageVirtualFile) file).getNode();
    }

    @Override
    public SeekableByteChannel getChannel() throws IOException {
        return new SeekableInMemoryByteChannel(getReader().getResource(getNode()));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getReader().getResourceStream(getNode().getLocation());
    }
}
