package org.glavo.viewer.file;

import java.io.IOException;

public class RootContainer extends Container {
    public static final RootContainer CONTAINER = new RootContainer();

    private RootContainer() {
        super(null);
        this.increment(); // should not be closed
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }
}
