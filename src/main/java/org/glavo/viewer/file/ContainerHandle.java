package org.glavo.viewer.file;

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.ForceCloseable;
import org.glavo.viewer.util.SilentlyCloseable;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class ContainerHandle implements SilentlyCloseable, ForceCloseable {
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

    private boolean closed = false;

    private synchronized void close(boolean force) {
        if (closed) return;
        closed = true;

        synchronized (container) {
            if (!container.containerHandles.remove(this)) {
                throw new AssertionError("handle=" + this);
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
