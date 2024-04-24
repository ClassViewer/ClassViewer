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
import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.file.roots.local.LocalFileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class JImageFileType extends ContainerFileType {
    public static final JImageFileType TYPE = new JImageFileType();

    private JImageFileType() {
        super("jimage", Set.of());
    }

    @Override
    public boolean check(VirtualFile file, String ext) {
        return file.getFileName().equals("modules");
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws IOException {
        Path file;
        if (handle instanceof LocalFileHandle localFileHandle) {
            file = localFileHandle.getPath();
        } else {
            Path tempFile = Files.createTempFile("viewer-", ".jimage");
            tempFile.toFile().deleteOnExit();

            try (InputStream input = handle.getInputStream()) {
                Files.copy(input, tempFile);
            }

            file = tempFile;
        }

        ImageReader reader = ImageReader.open(file);
        handle.setOnForceClose(reader::close);
        return new JImageContainer(handle, reader);
    }
}
