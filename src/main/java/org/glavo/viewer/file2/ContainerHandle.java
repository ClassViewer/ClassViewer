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
import org.glavo.viewer.util.ForceCloseable;
import org.glavo.viewer.util.SilentlyCloseable;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class ContainerHandle implements SilentlyCloseable, ForceCloseable {
    private final Container container;
    private CheckedRunnable<?> onForceClose;

    public ContainerHandle(Container container) {
        this.container = container;
        synchronized (container) {
            container.containerHandles.add(this);
        }
    }

    public Container getContainer() {
        return container;
    }

    public synchronized void setOnForceClose(CheckedRunnable<?> onForceClose) {
        this.onForceClose = onForceClose;
    }

    private volatile boolean closed = false;

    private synchronized void close(boolean force) {
        if (closed) return;

        synchronized (container) {
            if (closed) return;
            closed = true;

            if (!container.containerHandles.remove(this)) {
                throw new IllegalStateException("handle=" + this);
            }

            if (force) {
                LOGGER.info("Force close handle " + this);

                if (onForceClose != null) {
                    try {
                        onForceClose.runChecked();
                    } catch (Throwable e) {
                        LOGGER.log(Level.WARNING, "Failed to close " + this);
                    }
                }
            } else {
                LOGGER.info("Close handle " + this);
            }

            container.checkStatus();
        }
    }

    @Override
    public synchronized void close() {
        close(false);
    }

    @Override
    public void forceClose() {
        close(true);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + container + "]";
    }
}
