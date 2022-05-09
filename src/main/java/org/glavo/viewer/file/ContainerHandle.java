package org.glavo.viewer.file;

import org.glavo.viewer.util.SilentlyCloseable;

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

    @Override
    public void close() {
        LOGGER.info("Release handle " + this);
        synchronized (container) {
            if (!container.containerHandles.remove(this)) {
                throw new AssertionError();
            }
            container.checkStatus();
        }
    }
}
