package org.glavo.viewer.file.types.java.classfile.datatype;

import kala.function.CheckedFunction;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;

import java.io.IOException;

public class Table<C extends ClassFileComponent> extends ClassFileComponent {
    public static <C extends ClassFileComponent> Table<C> readFrom(
            ClassFileReader reader, UInt length, CheckedFunction<ClassFileReader, C, IOException> e) {
        throw new UnsupportedOperationException(); // TODO
    }
}
