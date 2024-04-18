/*
 * Copyright (C) 2024 Glavo. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.glavo.viewer.file2;

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.SilentlyCloseable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class FileHandle implements SilentlyCloseable {

    protected final VirtualFile file;

    private volatile boolean closed = false;
    private volatile CheckedRunnable<?> onForceClose;

    protected FileHandle(VirtualFile file) {
        this.file = file;
    }

    public void setOnForceClose(CheckedRunnable<?> onForceClose) {
        this.onForceClose = onForceClose;
    }

    public final @NotNull VirtualFile getFile() {
        return file;
    }

    public abstract boolean exists();

    public abstract boolean isReadonly();

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public abstract SeekableByteChannel openChannel() throws IOException;

    public SeekableByteChannel openWritableChannel() throws IOException {
        throw new UnsupportedOperationException();
    }

    public InputStream openInputStream() throws IOException {
        return Channels.newInputStream(openChannel());
    }

    public byte[] readAllBytes() throws IOException {
        try (InputStream in = openInputStream()) {
            return in.readAllBytes();
        }
    }

    public OutputStream openOutputStream() throws IOException {
        return Channels.newOutputStream(openWritableChannel());
    }

    public void write(byte[] bytes) throws IOException {
        try (OutputStream out = openOutputStream()) {
            out.write(bytes);
        }
    }

    protected void closeImpl() throws Exception {
    }

    void close(boolean force) {
        if (closed) {
            return;
        }

        Container container = file.getContainer();
        container.lock();
        try {
            if (closed) {
                return;
            }
            closed = true;
            LOGGER.info("Close handle " + this);

            if (force && onForceClose != null) {
                try {
                    onForceClose.runChecked();
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to run onForceClose", e);
                }
            }

            try {
                this.closeImpl();
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "Failed to close " + this, e);
            }

            if (container.fileHandles.remove(file) != this) {
                throw new AssertionError();
            }
            container.checkStatus();
        } finally {
            container.unlock();
        }
    }

    @Override
    public final void close() {
        close(false);
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return super.toString() + "[file=" + this.getFile() + "]";
    }
}
