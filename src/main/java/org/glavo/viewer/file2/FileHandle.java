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

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.ForceCloseable;
import org.glavo.viewer.util.SilentlyCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class FileHandle implements SilentlyCloseable, ForceCloseable {

    private final VirtualFile file;
    private volatile CheckedRunnable<?> onForceClose;

    protected FileHandle(Container container, VirtualFile file) {
        this.file = file;
    }

    public synchronized void setOnForceClose(CheckedRunnable<?> onForceClose) {
        this.onForceClose = onForceClose;
    }

    public VirtualFile getFile() {
        return file;
    }

    public abstract boolean exists();

    public abstract boolean isReadonly();

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

    private boolean closed = false;

    private synchronized void close(boolean force) {
        if (closed) {
            return;
        }
        closed = true;

        synchronized (file.getContainer()) {
            LOGGER.info("Close handle " + this);

            if (force && onForceClose != null) {
                try {
                    onForceClose.runChecked();
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to force close " + this, e);
                }
            }

            try {
                this.closeImpl();
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "Failed to close " + this, e);
            }

            FileHandle h;
            if ((h = file.getContainer().fileHandles.remove(getFile())) != this) {
                throw new AssertionError(String.format("expected=%s, actual=%s", this, h));
            }
            file.getContainer().checkStatus();
        }
    }

    @Override
    public final synchronized void close() {
        close(false);
    }

    @Override
    public void forceClose() {
        close(true);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[file=" + this.getFile() + "]";
    }
}
