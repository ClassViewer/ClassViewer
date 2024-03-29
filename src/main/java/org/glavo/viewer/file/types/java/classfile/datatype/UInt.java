package org.glavo.viewer.file.types.java.classfile.datatype;

public abstract class UInt extends IntComponent {
    protected UInt(int length, int value) {
        super(length, value);
    }

    @Override
    public String contentToString() {
        return Integer.toUnsignedString(getIntValue());
    }
}
