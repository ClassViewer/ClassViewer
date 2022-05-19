package org.glavo.viewer.util;

import kala.Conditions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArrayList implements ByteList {
    static final int DEFAULT_CAPACITY = 64;
    static final byte[] DEFAULT_EMPTY_ARRAY = new byte[0];

    byte[] elements;
    int size;

    private ByteArrayList(byte[] elements, int size) {
        this.elements = elements;
        this.size = size;
    }

    public ByteArrayList() {
        this(DEFAULT_EMPTY_ARRAY, 0);
    }

    public ByteArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("illegal initialCapacity: " + initialCapacity);
        }

        this.elements = initialCapacity == 0 ? DEFAULT_EMPTY_ARRAY : new byte[initialCapacity];
        this.size = 0;
    }


    // Unsafe
    public static ByteArrayList wrap(byte[] values) {
        return new ByteArrayList(values, values.length);
    }

    private void grow() {
        grow(size + 1);
    }

    private void grow(int minCapacity) {
        byte[] newArray = growArray(minCapacity);
        if (elements.length != 0) {
            System.arraycopy(elements, 0, newArray, 0, size);
        }
        elements = newArray;
    }

    private byte[] growArray(int minCapacity) {
        int oldCapacity = elements.length;
        if (elements == DEFAULT_EMPTY_ARRAY && oldCapacity == 0) {
            return new byte[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }

        int newCapacity = Math.max(Math.max(oldCapacity, minCapacity), oldCapacity + (oldCapacity >> 1));
        return new byte[newCapacity];
    }


    public final int size() {
        return size;
    }

    @Override
    public byte get(int index) {
        Conditions.checkElementIndex(index, size);
        return elements[index];
    }

    @Override
    public void set(int index, byte newValue) {
        Conditions.checkElementIndex(index, size);
        elements[index] = newValue;
    }

    @Override
    public void append(byte value) {
        if (size == elements.length) {
            grow();
        }
        elements[size++] = value;
    }

    @Override
    public void appendAll(byte[] values) {
        if (values.length == 0) return;

        if (elements.length - size < values.length)
            grow(size + values.length);

        System.arraycopy(values, 0, elements, size, elements.length);
        this.size += elements.length;
    }

    @Override
    public byte removeAt(int index) {
        Conditions.checkElementIndex(index, size);
        byte oldValue = elements[index];
        int newSize = size - 1;
        if (newSize > index) {
            System.arraycopy(elements, index + 1, elements, index, newSize - index);
        }
        elements[newSize] = 0;
        size = newSize;
        return oldValue;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(elements, 0, size);
    }

    @Override
    public String toString() {
        return joinToString(", ", "ByteArrayList[", "]");
    }
}
