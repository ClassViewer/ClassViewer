package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;

/*
CONSTANT_InterfaceMethodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
*/
public final class ConstantInterfaceMethodrefInfo extends ConstantRefInfo {
    public ConstantInterfaceMethodrefInfo(Tag tag, CpIndex<ConstantClassInfo> classIndex, CpIndex<ConstantNameAndTypeInfo> nameAndTypeIndex) {
        super(tag, classIndex, nameAndTypeIndex);
    }
}
