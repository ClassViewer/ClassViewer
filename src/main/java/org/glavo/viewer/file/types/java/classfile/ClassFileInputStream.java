package org.glavo.viewer.file.types.java.classfile;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.InputStream;

public class ClassFileInputStream extends DataInputStream {
    public ClassFileInputStream(@NotNull InputStream in) {
        super(in);
    }


}
