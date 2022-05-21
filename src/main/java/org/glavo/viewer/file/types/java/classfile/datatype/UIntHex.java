package org.glavo.viewer.file.types.java.classfile.datatype;

import java.util.Locale;

public class UIntHex extends UInt {
    protected UIntHex(int length, int value) {
        super(length, value);
    }

    @Override
    public String contentToString() {
        return "0x" + Integer.toHexString(getIntValue()).toUpperCase(Locale.ROOT);
    }
}
