package org.glavo.viewer.file.types.java.classfile.datatype;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;

import java.util.Arrays;

public class Bytes extends ClassFileComponent {
    private final byte[] values;

    public Bytes(byte[] values) {
        this.values = values;

        this.setLength(values.length);
    }

    @Override
    public String contentToString() {
        return Arrays.toString(values);
    }

    @Override
    protected boolean isLeafComponent() {
        return true;
    }
}
