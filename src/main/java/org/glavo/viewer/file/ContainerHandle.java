package org.glavo.viewer.file;

import kala.function.CheckedRunnable;
import org.glavo.viewer.util.ForceCloseable;
import org.glavo.viewer.util.SilentlyCloseable;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

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
                        LOGGER.warning("Failed to close " + this);
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
