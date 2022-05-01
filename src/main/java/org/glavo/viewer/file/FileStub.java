package org.glavo.viewer.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileStub {

    boolean exists();

    boolean isReadonly();

    InputStream openInputStream() throws IOException;

    default byte[] readBytes() throws IOException {
        try (InputStream in = openInputStream()) {
            return in.readAllBytes();
        }
    }

    OutputStream openOutputStream() throws IOException;
}
