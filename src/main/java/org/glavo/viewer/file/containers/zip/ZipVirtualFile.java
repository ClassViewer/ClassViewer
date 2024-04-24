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
package org.glavo.viewer.file.containers.zip;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.glavo.viewer.file.AbstractVirtualFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class ZipVirtualFile extends AbstractVirtualFile<ZipVirtualFile> {
    ZipArchiveEntry entry;
    Map<String, ZipVirtualFile> children;

    ZipVirtualFile(ZipContainer container, ZipVirtualFile parent, List<String> name) {
        super(container, parent, name);
    }

    public ZipArchiveEntry getEntry() {
        return entry;
    }

    @Override
    public boolean isDirectory() {
        return entry == null || entry.isDirectory();
    }

    @Override
    public List<ZipVirtualFile> listFiles() throws IOException {
        if (!isDirectory()) {
            throw new IOException(this + " is not a directory");
        }
        return children != null ? List.copyOf(children.values()) : null;
    }
}
