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

import org.glavo.viewer.resources.Images;

import java.util.Set;

public final class DirectoryFileType extends FileType {

    public static final DirectoryFileType TYPE = new DirectoryFileType();

    private DirectoryFileType() {
        super("directory", Images.folder, Set.of());
    }

    @Override
    public boolean check(VirtualFile file, String ext) {
        return file.isDirectory();
    }
}