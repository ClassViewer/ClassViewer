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

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public abstract class Container {

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

    public boolean isLocked() {
        return lock.isLocked();
    }

    public void lock() {
        Container parent = getParent();
        if (parent != null) {
            parent.lock();
        }

        lock.lock();
    }

    public void unlock() {
        Container parent = getParent();
        if (parent != null) {
            parent.unlock();
        }

        lock.unlock();
    }

    public boolean hasMultiRoots() {
        return false;
    }

    public abstract VirtualFile getRootDirectory();

    public List<VirtualFile> getRootDirectories() {
        if (hasMultiRoots()) {
            throw new UnsupportedOperationException("Must be overridden");
        }

        return List.of(getRootDirectory());
    }

    public @Nullable Container getParent() {
        return handle != null ? handle.getFile().getContainer() : null;
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

            if (file.getContainer() != this) {
                throw new IOException("Container mismatch");
            }

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

    public final Container getSubContainer(TypedVirtualFile file) throws IOException {
        lock();

        try {
            ensureOpen();
            Container subContainer = subContainers.get(file.file());
            if (subContainer != null) {
                return subContainer;
            }

            if (fileHandles.containsKey(file.file())) {
                throw new IOException("File " + file + " is already open");
            }

            if (!(file.type() instanceof ContainerFileType containerFileType)) {
                throw new AssertionError("type: " + file.type());
            }

            FileHandle fileHandle = openFile(file.file());
            try {
                subContainer = containerFileType.openContainerImpl(fileHandle);
                subContainers.put(file.file(), subContainer);
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

            Container parent = getParent();
            LOGGER.info("Close container " + this);

            if (parent != null) {
                parent.subContainers.remove(handle.getFile());
            }

            for (Container subContainer : subContainers.values().toArray(Container[]::new)) {
                subContainer.forceClose();
            }
            for (FileHandle handle : this.fileHandles.values().toArray(FileHandle[]::new)) {
                handle.close(true);
            }
            for (ContainerHandle handle : this.containerHandles.toArray(ContainerHandle[]::new)) {
                handle.close(true);
            }

            assert subContainers.isEmpty();
            assert fileHandles.isEmpty();
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
