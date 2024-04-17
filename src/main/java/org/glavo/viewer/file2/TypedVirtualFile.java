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

import java.io.IOException;
import java.util.List;

public record TypedVirtualFile(VirtualFile file, FileType type) {
    public static TypedVirtualFile of(VirtualFile file) {
        return new TypedVirtualFile(file, FileType.detectFileType(file));
    }

    public String getFileName() {
        return file.getFileName();
    }

    public boolean isDirectory() {
        return type == DirectoryFileType.TYPE;
    }

    public boolean isContainer() {
        return type instanceof ContainerFileType;
    }

    public List<TypedVirtualFile> listFiles() throws IOException {
        List<VirtualFile> list = file.listFiles();
        TypedVirtualFile[] result = new TypedVirtualFile[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = of(list.get(i));
        }
        return List.of(result);
    }
}
