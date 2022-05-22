package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;

/*
CONSTANT_Methodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
*/
public final class ConstantMethodrefInfo extends ConstantInfo {
    public ConstantMethodrefInfo(ConstantInfo.Tag tag, CpIndex<ConstantClassInfo> classIndex, CpIndex<ConstantNameAndTypeInfo> nameAndTypeIndex) {
        super(tag);
        classIndex.setName("class_index");
        nameAndTypeIndex.setName("name_and_type_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, classIndex, nameAndTypeIndex);
    }
}
