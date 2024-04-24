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

import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;

public final class JImageContainer extends Container {
    private static final int offset = "/modules/".length();

    private final ImageReader reader;

    private final JImageVirtualFile root;

    JImageContainer(FileHandle handle, ImageReader reader) throws IOException {
        super(handle);
        this.reader = reader;

        root = new JImageVirtualFile(this, null, reader.getRootDirectory());
    }

    public ImageReader getReader() {
        return reader;
    }

    @Override
    public VirtualFile getRootDirectory() {
        return root;
    }

    @Override
    protected FileHandle openFileImpl(VirtualFile file) throws IOException {
        if (!(file instanceof JImageVirtualFile jimageVirtualFile)) {
            throw new IllegalArgumentException("File: " + file);
        }

        return new JImageFileHandle(jimageVirtualFile);
    }
}
