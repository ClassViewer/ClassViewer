package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
CONSTANT_Float_info {
    u1 tag;
    u4 bytes;
}
*/
public final class ConstantFloatInfo extends ConstantInfo {
    public ConstantFloatInfo(U1 tag, U4 bytes) {
        super(tag);
        bytes.setName("bytes");

        this.getChildren().setAll(tag, bytes);
    }
}
