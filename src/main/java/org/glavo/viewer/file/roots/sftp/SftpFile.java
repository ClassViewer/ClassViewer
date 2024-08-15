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

import org.apache.sshd.sftp.client.SftpClient;
import org.glavo.viewer.file.AbstractVirtualFile;
import org.glavo.viewer.file.Container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SftpFile extends AbstractVirtualFile<SftpFile> {

    private final boolean isDirectory;
    private final String fullPath;

    SftpFile(Container container, SftpFile parent, String fullPath, List<String> name, boolean isDirectory) {
        super(container, parent, name);
        this.isDirectory = isDirectory;
        this.fullPath = fullPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public List<SftpFile> listFiles() throws IOException {
        if (!isDirectory) {
            throw new IOException("Not a directory");
        }

        SftpRootContainer container = (SftpRootContainer) this.container;
        container.lock();
        try {
            container.ensureOpen();
            SftpClient client = container.client;

            var result = new ArrayList<SftpFile>();

            try (SftpClient.CloseableHandle closeableHandle = client.openDir(fullPath)) {
                for (SftpClient.DirEntry entry : client.listDir(closeableHandle)) {
                    if (entry.getFilename().equals(".") || entry.getFilename().equals("..")) {
                        continue;
                    }

                    String[] arr = this.name.toArray(new String[this.name.size() + 1]);
                    arr[arr.length - 1] = entry.getFilename();

                    String newFullPath = this.fullPath.equals("/") ? "/" + entry.getFilename() : this.fullPath + "/" + entry.getFilename();

                    result.add(new SftpFile(container, this, newFullPath, List.of(arr), entry.getAttributes().isDirectory()));
                }
            }

            return result;
        } finally {
            container.unlock();
        }
    }

    @Override
    public String toString() {
        return "SftpFile{fullPath='%s', isDirectory=%s}".formatted(fullPath, isDirectory);
    }
}
