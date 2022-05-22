package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_Fieldref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
*/
public final class ConstantFieldrefInfo extends ConstantInfo {
    public ConstantFieldrefInfo(ConstantInfo.Tag tag, U2 classIndex, U2 nameAndTypeIndex) {
        super(tag);
        classIndex.setName("class_index");
        nameAndTypeIndex.setName("name_and_type_index");

        this.getChildren().setAll(tag, classIndex, nameAndTypeIndex);
    }
}
