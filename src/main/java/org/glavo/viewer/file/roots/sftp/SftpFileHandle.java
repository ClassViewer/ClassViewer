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
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

public final class SftpFileHandle extends FileHandle {

    private final SftpClient.CloseableHandle handle;

    SftpFileHandle(VirtualFile file, SftpClient.CloseableHandle handle) {
        super(file);
        this.handle = handle;
    }

    @Override
    protected void closeImpl() throws Exception {
        handle.close();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        SftpFile file = (SftpFile) this.file;
        var container = ((SftpRootContainer) file.getContainer());

        container.lock();
        try {
            container.ensureOpen();
            return Channels.newInputStream(container.client.openRemoteFileChannel(handle.getPath()));
        } finally {
            container.unlock();
        }
    }
}
