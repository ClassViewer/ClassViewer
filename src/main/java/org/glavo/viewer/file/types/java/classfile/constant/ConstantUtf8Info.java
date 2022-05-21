package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_Utf8_info {
    u1 tag;
    u2 length;
    u1 bytes[length];
}
*/
public final class ConstantUtf8Info extends ConstantInfo {
    public ConstantUtf8Info(U1 tag, U2 length, Bytes bytes) {
        super(tag);
        length.setName("length");
        bytes.setName("bytes");

        this.getChildren().setAll(tag, length, bytes);
    }

    private static final class Mutf8 extends ClassFileComponent {

    }
}
