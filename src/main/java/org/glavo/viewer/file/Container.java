package org.glavo.viewer.file;

import java.io.Closeable;
import java.io.IOException;

public interface Container extends Closeable {

    FilePath getPath();

    void close() throws IOException;
}
