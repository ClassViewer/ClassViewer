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

import org.glavo.viewer.util.ForceCloseable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public abstract class Container implements ForceCloseable {

    private final FileHandle handle;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean closed = false;

    final Set<ContainerHandle> containerHandles = new HashSet<>();
    final Map<VirtualFile, FileHandle> fileHandles = new HashMap<>();
    final Map<VirtualFile, Container> subContainers = new HashMap<>();

    protected Container(FileHandle handle) {
        this.handle = handle;
    }

    public final void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Container is already closed");
        }
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public VirtualFile getPath() {
        return handle.getFile();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public final FileHandle openFile(VirtualFile file) throws IOException {
        lock();
        try {
            ensureOpen();

            if (file.getContainer() != this)
                throw new IOException("Container mismatch");

            if (fileHandles.containsKey(file)) {
                throw new IOException("File " + file + " is already open");
            }

            FileHandle handle = openFileImpl(file);
            fileHandles.put(file, handle);
            return handle;
        } finally {
            unlock();
        }
    }

    protected abstract FileHandle openFileImpl(VirtualFile file) throws IOException;

    public final Container getSubContainer(VirtualFile file) throws IOException {
        lock();

        try {
            ensureOpen();
            Container subContainer = subContainers.get(file);
            if (subContainer != null) {
                return subContainer;
            }

            if (fileHandles.containsKey(file)) {
                throw new IOException("File " + file + " is already open");
            }

            if (file.isDirectory())
                throw new IOException("File " + file + " is a directory");

            FileType type = FileType.detectFileType(file);
            if (!(type instanceof ContainerFileType containerFileType))
                throw new IOException("File " + file + " is not a container");

            FileHandle fileHandle = openFile(file);
            try {
                subContainer = containerFileType.openContainerImpl(fileHandle);
                subContainers.put(file, subContainer);
                return subContainer;
            } catch (Throwable e) {
                fileHandle.close();
                throw e;
            }
        } finally {
            unlock();
        }
    }

    public boolean isReadonly() {
        return true;
    }

    protected void closeImpl() throws Exception {
    }

    void checkStatus() {
        lock();
        try {
            if (fileHandles.isEmpty() && containerHandles.isEmpty()) {
                forceClose();
            }
        } finally {
            unlock();
        }
    }

    public void forceClose() {
        lock();
        try {
            if (closed) {
                return;
            }
            closed = true;

            LOGGER.info("Close container " + this);

            if (handle != null) {
                Container parent = handle.getFile().getContainer();
                parent.lock();
                try {
                    if (parent.subContainers.remove(handle.getFile()) != this) {
                        throw new AssertionError();
                    }
                } finally {
                    parent.unlock();
                }
            }

            for (Container subContainer : subContainers.values().toArray(Container[]::new)) {
                subContainer.forceClose();
            }

            assert subContainers.isEmpty();

            for (FileHandle handle : this.fileHandles.values().toArray(FileHandle[]::new)) {
                handle.close(true);
            }

            assert fileHandles.isEmpty();

            for (ContainerHandle handle : this.containerHandles.toArray(ContainerHandle[]::new)) {
                handle.close(true);
            }

            assert containerHandles.isEmpty();

            try {
                this.closeImpl();
            } catch (Throwable e) {
                LOGGER.warning("Failed to close " + this, e);
            } finally {
                if (handle != null) {
                    handle.close();
                }
            }
        } finally {
            unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("%s[handle=%s]", this.getClass().getSimpleName(), getFileHandle());
    }
}
