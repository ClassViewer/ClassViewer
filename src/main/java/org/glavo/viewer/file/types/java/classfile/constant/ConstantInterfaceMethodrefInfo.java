package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_InterfaceMethodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
*/
public final class ConstantInterfaceMethodrefInfo extends ConstantInfo {
    public ConstantInterfaceMethodrefInfo(U1 tag, U2 classIndex, U2 nameAndTypeIndex) {
        super(tag);
        classIndex.setName("class_index");
        nameAndTypeIndex.setName("name_and_type_index");

        this.getChildren().setAll(tag, classIndex, nameAndTypeIndex);
    }
}
