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
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public abstract class Container implements ForceCloseable {

    private static final Map<VirtualFile, Container> containers = new HashMap<>();

    private final FileHandle handle;

    final Set<FileHandle> fileHandles = new HashSet<>();
    final Set<ContainerHandle> containerHandles = new HashSet<>();

    protected Container(FileHandle handle) {
        this.handle = handle;
    }

    public static Container getContainer(VirtualFile file) throws Throwable {
        if (file == null) { // TODO: || file == LocalRoot.Path
            return null; // TODO: return LocalRootContainer.CONTAINER;
        }

        synchronized (Container.class) {
            return containers.computeIfAbsent(file, f -> {
                return null; // TODO
            });
        }
    }

    public VirtualFile getPath() {
        return handle.getFile();
    }

    public FileHandle getFileHandle() {
        return handle;
    }

    public final synchronized FileHandle openFile(VirtualFile file) throws IOException {
        if (closed) {
            throw new IllegalStateException(this + " is already closed");
        }

        FileHandle handle = openFileImpl(file);
        fileHandles.add(handle);
        return handle;
    }

    protected abstract FileHandle openFileImpl(VirtualFile file) throws IOException, NoSuchFileException;

    public final NavigableSet<VirtualFile> resolveFiles() throws Exception {
        return null; // TODO
    }

    public abstract Set<VirtualFile> list(VirtualFile dir) throws Throwable;

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

        synchronized (Container.class) {
            Container container = containers.remove(getPath());
            if (container != this) {
                throw new AssertionError(String.format("expected=%s, actual=%s", this, container));
            }
        }

        for (FileHandle handle : this.fileHandles.toArray(FileHandle[]::new)) {
            handle.forceClose();
        }
        if (!fileHandles.isEmpty()) {
            throw new AssertionError("handles=" + fileHandles);
        }

        for (ContainerHandle handle : this.containerHandles.toArray(ContainerHandle[]::new)) {
            handle.forceClose();
        }
        if (!this.containerHandles.isEmpty()) {
            throw new AssertionError("containerHandles=" + containerHandles);
        }

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
