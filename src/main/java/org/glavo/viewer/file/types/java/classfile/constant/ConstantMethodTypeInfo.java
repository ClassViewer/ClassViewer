package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_MethodType_info {
    u1 tag;
    u2 descriptor_index;
}
*/
public final class ConstantMethodTypeInfo extends ConstantInfo {
    public ConstantMethodTypeInfo(ConstantInfo.Tag tag, U2 descriptorIndex) {
        super(tag);
        descriptorIndex.setName("descriptor_index");

        this.getChildren().setAll(tag, descriptorIndex);
    }
}
