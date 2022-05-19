package org.glavo.viewer.util;

import kala.collection.base.primitive.ByteIterator;
import kala.collection.base.primitive.ByteTraversable;
import org.glavo.viewer.file.FileHandle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;

public interface ByteList extends ByteTraversable {

    @Override
    default @NotNull ByteIterator iterator() {
        final int size = this.size();

        return new ByteIterator() {
            int i;

            @Override
            public byte nextByte() {
                if (!hasNext()) throw new NoSuchElementException();

                return get(i++);
            }

            @Override
            public boolean hasNext() {
                return i < size;
            }
        };
    }

    @Override
    default int knownSize() {
        return size();
    }

    int size();

    byte get(int index);

    void set(int index, byte value);

    void append(byte value);

    default void appendAll(byte[] values) {
        for (byte value : values) {
            append(value);
        }
    }

    byte removeAt(int index);

    void clear();

    default void readFrom(FileHandle handle) throws IOException {
        appendAll(handle.readAllBytes());
    }
}
