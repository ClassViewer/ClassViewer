package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index;
    u2 descriptor_index;
}
*/
public final class ConstantNameAndTypeInfo extends ConstantInfo {
    public ConstantNameAndTypeInfo(U1 tag, U2 nameIndex, U2 descriptorIndex) {
        super(tag);
        nameIndex.setName("name_index");
        descriptorIndex.setName("descriptor_index");

        this.getChildren().setAll(tag, nameIndex, descriptorIndex);
    }
}
