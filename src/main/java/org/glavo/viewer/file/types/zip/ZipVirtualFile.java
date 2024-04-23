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
package org.glavo.viewer.file.types.zip;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ZipVirtualFile extends VirtualFile {
    final List<String> name;
    ZipArchiveEntry entry;

    ZipVirtualFile parent;
    Map<String, ZipVirtualFile> children;

    ZipVirtualFile(ZipContainer container, List<String> name) {
        super(container);
        this.name = name;
    }

    public ZipArchiveEntry getEntry() {
        return entry;
    }

    public boolean isRoot() {
        return name.isEmpty();
    }

    @Override
    public List<String> relativize(VirtualFile other) {
        if (!(other instanceof ZipVirtualFile otherZipVirtualFile) || this.container != otherZipVirtualFile.container) {
            throw new IllegalArgumentException();
        }
        return relativize(this.name, otherZipVirtualFile.name);
    }

    @Override
    public String getFileName() {
        return name.isEmpty() ? "" : name.getLast();
    }

    @Override
    public VirtualFile getParent() {
        return parent;
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
        return children != null ? new ArrayList<>(children.values()) : null;
    }
}
