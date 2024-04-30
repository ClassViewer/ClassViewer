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
package org.glavo.viewer.file;

import kala.function.CheckedRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.foreign.MemorySegment;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public abstract class FileHandle implements Runnable {

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

    public boolean isReadonly() {
        return true;
    }

    public boolean supportRandomAccess() {
        return false;
    }

    public SeekableByteChannel getChannel() throws IOException {
        throw new UnsupportedOperationException();
    }

    public abstract InputStream getInputStream() throws IOException;

    public MemorySegment readAllBytes() throws IOException {
        try (InputStream in = getInputStream()) {
            return MemorySegment.ofArray(in.readAllBytes());
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if (isReadonly()) {
            throw new IOException("File is readonly");
        }
        return Channels.newOutputStream(getChannel());
    }

    public void write(byte[] bytes) throws IOException {
        try (OutputStream out = getOutputStream()) {
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
                    LOGGER.warning("Failed to run onForceClose", e);
                }
            }

            try {
                this.closeImpl();
            } catch (Throwable e) {
                LOGGER.warning("Failed to close " + this, e);
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
    public final void run() {
        close(false);
    }

    public final void close() {
        run();
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
