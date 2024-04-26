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
package org.glavo.viewer.file.roots.sftp;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.util.List;

public final class SftpFile extends VirtualFile {
    SftpFile(Container container) {
        super(container);
    }

    @Override
    public List<String> relativize(VirtualFile other) {
        return List.of();
    }

    @Override
    public String getFileName() {
        return "";
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public List<? extends VirtualFile> listFiles() throws IOException {
        return List.of();
    }
}
