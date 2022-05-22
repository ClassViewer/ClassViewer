package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_InvokeDynamic_info {
    u1 tag;
    u2 bootstrap_method_attr_index;
    u2 name_and_type_index;
}
*/
public final class ConstantInvokeDynamicInfo extends ConstantInfo {
    public ConstantInvokeDynamicInfo(ConstantInfo.Tag tag, U2 bootstrapMethodAttrIndex, U2 nameAndTypeIndex) {
        super(tag);
        bootstrapMethodAttrIndex.setName("bootstrap_method_attr_index");
        nameAndTypeIndex.setName("name_and_type_index");

        this.getChildren().setAll(tag, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }
}
