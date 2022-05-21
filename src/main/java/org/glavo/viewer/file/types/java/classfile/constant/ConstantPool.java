package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;

import java.io.IOException;

public class ConstantPool extends ClassFileComponent {
    public static ConstantPool readFrom(ClassFileReader reader, int length) throws IOException {
        throw new UnsupportedOperationException(); // TODO
    }
}
