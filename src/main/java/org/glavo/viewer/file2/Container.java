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

import org.glavo.viewer.util.ForceCloseable;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container implements ForceCloseable {

    private final FileHandle handle;

    final Set<ContainerHandle> containerHandles = new HashSet<>();
    final Map<VirtualFile, FileHandle> fileHandles = new HashMap<>();
    final Map<VirtualFile, Container> subContainers = new HashMap<>();

    protected Container(FileHandle handle) {
        this.handle = handle;
    }

    protected final void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Container is already closed");
        }
    }

    public VirtualFile getPath() {
        return handle.getFile();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public final synchronized FileHandle openFile(VirtualFile file) throws IOException {
        ensureOpen();

        if (fileHandles.containsKey(file)) {
            throw new IOException("File " + file + " is already open");
        }

        FileHandle handle = openFileImpl(file);
        fileHandles.put(file, handle);
        return handle;
    }

    protected abstract FileHandle openFileImpl(VirtualFile file) throws IOException;

    public final synchronized Container getSubContainer(VirtualFile file) throws IOException {
        ensureOpen();
        Container subContainer = subContainers.get(file);
        if (subContainer != null) {
            return subContainer;
        }

        if (fileHandles.containsKey(file)) {
            throw new IOException("File " + file + " is already open");
        }

        FileHandle fileHandle = openFile(file);
        try {
            subContainer = null; // TODO
            subContainers.put(file, subContainer);
            return subContainer;
        } catch (Throwable e) {
            fileHandle.close();
            throw e;
        }
    }

    public abstract NavigableSet<VirtualFile> resolveFiles() throws IOException;

    public abstract List<VirtualFile> list(VirtualFile dir) throws IOException;

    public boolean isReadonly() {
        return true;
    }

    protected void closeImpl() throws Exception {
    }

    synchronized void checkStatus() {
        if (fileHandles.isEmpty() && containerHandles.isEmpty()) {
            forceClose();
        }
    }

    private volatile boolean closed = false;

    public synchronized void forceClose() {
        if (closed) {
            return;
        }
        closed = true;

        LOGGER.info("Close container " + this);

        if (handle != null) {
            Container parent = handle.getFile().getContainer();
            synchronized (parent) {
                if (parent.subContainers.remove(handle.getFile()) != this) {
                    throw new AssertionError();
                }
            }
        }

        for (Container subContainer : subContainers.values().toArray(Container[]::new)) {
            subContainer.forceClose();
        }

        assert subContainers.isEmpty();

        for (FileHandle handle : this.fileHandles.values().toArray(FileHandle[]::new)) {
            handle.forceClose();
        }

        assert fileHandles.isEmpty();

        for (ContainerHandle handle : this.containerHandles.toArray(ContainerHandle[]::new)) {
            handle.forceClose();
        }

        assert containerHandles.isEmpty();

        try {
            this.closeImpl();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to close " + this, e);
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s[handle=%s]", this.getClass().getSimpleName(), getFileHandle());
    }
}
