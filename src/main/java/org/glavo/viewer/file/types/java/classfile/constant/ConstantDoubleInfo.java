package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
CONSTANT_Double_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}
*/
public final class ConstantDoubleInfo extends ConstantInfo {
    public ConstantDoubleInfo(U1 tag, U4 highBytes, U4 lowBytes) {
        super(tag);
        highBytes.setName("high_bytes");
        lowBytes.setName("low_bytes");

        this.getChildren().setAll(tag, highBytes, lowBytes);
    }
}
