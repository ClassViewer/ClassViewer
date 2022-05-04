package org.glavo.viewer.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;

public abstract class VirtualFile {

    public abstract boolean exists();

    public abstract boolean isReadonly();

    public abstract SeekableByteChannel openChannel() throws IOException;

    public abstract SeekableByteChannel openWritableChannel() throws IOException;

    public InputStream openInputStream() throws IOException {
        return Channels.newInputStream(openChannel());
    }

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

    public OutputStream openOutputStream() throws IOException {
        return Channels.newOutputStream(openWritableChannel());
    }

    public void write(byte[] bytes) throws IOException {
        try (OutputStream out = openOutputStream()) {
            out.write(bytes);
        }
    }
}
