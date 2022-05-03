package org.glavo.viewer.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class VirtualFile {

    public abstract boolean exists();

    public abstract boolean isReadonly();

    public abstract InputStream openInputStream() throws IOException;

    public byte[] readAllBytes() throws IOException {
        try (InputStream in = openInputStream()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();
        }
    }

    public abstract OutputStream openOutputStream() throws IOException;

    public void write(byte[] bytes) throws IOException {
        try (OutputStream out = openOutputStream()) {
            out.write(bytes);
        }
    }
}
