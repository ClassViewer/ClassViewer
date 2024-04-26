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

import org.glavo.viewer.file.VirtualRoot;

import java.util.Objects;

public final class SftpRoot extends VirtualRoot {
    private final String host;
    private final int port;
    private final String userName;

    SftpRoot(String host, int port, String userName) {
        this.host = host;
        this.port = port;
        this.userName = userName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SftpRoot other)) return false;
        return port == other.port && host.equals(other.host) && userName.equals(other.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, userName);
    }

    @Override
    public String toString() {
        return "SftpRoot{host=%s, port=%d, userName=%s}".formatted(host, port, userName);
    }
}
