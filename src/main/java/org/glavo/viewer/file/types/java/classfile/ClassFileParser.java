package org.glavo.viewer.file.types.java.classfile;

import org.glavo.viewer.util.ByteList;

public class ClassFileParser {
    private ByteList bytes;
    private int offset;

    public ClassFileParser(ByteList bytes) {
        this.bytes = bytes;
    }
}
