package org.glavo.viewer.file;

import org.glavo.viewer.util.SilentlyCloseable;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class ContainerHandle implements SilentlyCloseable {
    private final Container container;

    public ContainerHandle(Container container) {
        this.container = container;
        synchronized (container) {
            container.containerHandles.add(this);
        }
    }

    public Container getContainer() {
        return container;
    }

    public void closeImpl() throws Throwable {
    }

    @Override
    public synchronized void close() {
        LOGGER.info("Release handle " + this);
        synchronized (container) {
            if (!container.containerHandles.remove(this)) {
                throw new AssertionError();
            }

            try {
                closeImpl();
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "Failed to close " + this);
            }

            container.checkStatus();
        }
    }

    protected void forceCloseImpl() {
    }
}
