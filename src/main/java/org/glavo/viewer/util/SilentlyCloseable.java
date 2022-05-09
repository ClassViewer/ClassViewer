package org.glavo.viewer.util;

import java.io.Closeable;

public interface SilentlyCloseable extends Closeable {
    @Override
    void close();
}
