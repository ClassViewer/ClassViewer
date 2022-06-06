package org.glavo.viewer.file.types.java.classfile.datatype;

public abstract class SInt extends IntComponent {
    protected SInt(int length, int value) {
        super(length, value);
    }

    @Override
    public String contentToString() {
        return Integer.toString(getIntValue());
    }
}
