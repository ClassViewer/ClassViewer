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
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public final class ZipContainer extends Container {

    private static void add(ZipVirtualFile root, ZipArchiveEntry entry) {
        String[] split = entry.getName().split("/");
        if (split.length == 0) {
            return;
        }

        for (String element : split) {
            if (element.isEmpty() || element.equals(".") || element.equals("..")) {
                return;
            }
        }


        // TODO
//
//        ZipVirtualFile current = root;
//        for (int i = 0; i < name.size(); i++) {
//            String element = name.get(i);
//            if (current.children == null) {
//                current.children = new HashMap<>();
//            }
//
//            ZipVirtualFile f = current.children.get(element);
//            if (f == null) {
//
//            } else {
//
//            }
//        }
    }

    final ZipFile zipFile;

    ZipContainer(FileHandle handle, ZipFile zipFile) {
        super(handle);
        this.zipFile = zipFile;

        ZipVirtualFile root = new ZipVirtualFile(this, List.of());

        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            add(root, entry);
        }
    }

    @Override
    protected FileHandle openFileImpl(VirtualFile file) throws IOException {

        return null;
    }

    @Override
    protected void closeImpl() throws Exception {
        zipFile.close();
    }
}
