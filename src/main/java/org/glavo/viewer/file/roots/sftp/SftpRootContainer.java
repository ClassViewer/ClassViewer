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

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.RootContainer;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.util.IOUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public final class SftpRootContainer extends RootContainer {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    public static SftpRootContainer connect(SftpRoot root, String password) throws IOException {
        SshClient client = null;
        try {
            client = SshClient.setUpDefaultClient();
            client.start();

            ClientSession session = client.connect(root.getUserName(), root.getHost(), root.getPort()).verify(TIMEOUT).getClientSession();
            session.addPasswordIdentity(password);
            session.auth().verify();

            return new SftpRootContainer(root, SftpClientFactory.instance().createSftpClient(session));
        } catch (Throwable e) {
            IOUtils.closeQuietly(client);
            throw e;
        }
    }

    private final SftpRoot root;
    final SftpClient client;

    SftpRootContainer(SftpRoot root, SftpClient client) {
        super(null);
        this.root = root;
        this.client = client;
    }

    @Override
    public void ensureOpen() throws IOException {
        super.ensureOpen();
        if (!client.isOpen()) {
            throw new IOException("SFTP client is closed");
        }
    }

    @Override
    public SftpRoot getRoot() {
        return root;
    }

    @Override
    public SftpVirtualFile getRootDirectory() {
        return new SftpVirtualFile(this, null, "/", List.of(), true);
    }

    @Override
    protected FileHandle openFileImpl(VirtualFile file) throws IOException {
        if (!(file instanceof SftpVirtualFile sftpVirtualFile)) {
            throw new IllegalArgumentException();
        }

        if (file.isDirectory()) {
            throw new IOException("File " + file + "is a directory");
        }

        return new SftpFileHandle(file, client.open(sftpVirtualFile.getFileName()));
    }
}
